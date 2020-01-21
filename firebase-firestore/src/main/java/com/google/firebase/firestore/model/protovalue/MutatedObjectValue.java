// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.firebase.firestore.model.protovalue;

import static com.google.firebase.firestore.util.Assert.hardAssert;

import androidx.annotation.Nullable;
import com.google.firebase.database.collection.ImmutableSortedMap;
import com.google.firebase.firestore.model.FieldPath;
import com.google.firebase.firestore.model.value.FieldValue;
import com.google.firebase.firestore.util.SortedMapValueIterator;
import com.google.firestore.v1.Value;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public class MutatedObjectValue extends ObjectValue
    implements Iterable<Map.Entry<String, FieldValue>> {
  private static final ImmutableSortedMap<String, FieldValue> EMPTY_STRING_VALUE_MAP =
      ImmutableSortedMap.Builder.emptyMap(String::compareTo);

  private final ImmutableSortedMap<String, FieldValue> overlays;

  public MutatedObjectValue(Value internalValue, ImmutableSortedMap<String, FieldValue> overlays) {
    super(internalValue);
    this.overlays = overlays;
  }

  public MutatedObjectValue(Value internalValue) {
    this(internalValue, EMPTY_STRING_VALUE_MAP);
  }

  @Override
  public int typeOrder() {
    return TYPE_ORDER_OBJECT;
  }

  @Nullable
  @Override
  public Object value() {
    Map<String, Object> result = new HashMap<>();
    for (Map.Entry<String, FieldValue> entry : this) {
      result.put(entry.getKey(), entry.getValue().value());
    }
    return result;
  }

  @Override
  public MutatedObjectValue set(FieldPath path, FieldValue value) {
    hardAssert(!path.isEmpty(), "Cannot set field for empty path on ObjectValue");
    String childName = path.getFirstSegment();
    if (path.length() == 1) {
      return setChild(childName, value);
    } else {
      FieldValue child = get(FieldPath.fromSingleSegment(childName));
      if (!(child instanceof MutatedObjectValue)) {
        Value baseValue =
            child instanceof ObjectValue
                ? ((ObjectValue) child).internalValue
                : Value.newBuilder()
                    .setMapValue(com.google.firestore.v1.MapValue.getDefaultInstance())
                    .build();
        child = new MutatedObjectValue(baseValue);
      }
      child = ((MutatedObjectValue) child).set(path.popFirst(), value);
      return setChild(childName, child);
    }
  }

  @Override
  public MutatedObjectValue delete(FieldPath path) {
    hardAssert(!path.isEmpty(), "Cannot delete field for empty path on ObjectValue");
    String childName = path.getFirstSegment();
    if (path.length() == 1) {
      return new MutatedObjectValue(internalValue, overlays.insert(childName, null));
    } else {
      FieldValue child = get(FieldPath.fromSingleSegment(childName));
      if (child instanceof MutatedObjectValue) {
        MutatedObjectValue newChild = ((MutatedObjectValue) child).delete(path.popFirst());
        return setChild(childName, newChild);
      } else if (child instanceof ObjectValue) {
        MutatedObjectValue newChild =
            new MutatedObjectValue(((ObjectValue) child).internalValue).delete(path.popFirst());
        return setChild(childName, newChild);

      } else {
        // Don't actually change a primitive value to an object for a delete.
        return this;
      }
    }
  }

  @Override
  public @Nullable FieldValue get(FieldPath path) {
    if (path.isEmpty()) {
      return this;
    }

    String childName = path.getFirstSegment();

    if (overlays.containsKey(childName)) {
      FieldValue fieldValue = overlays.get(childName);
      if (path.length() == 1) {
        return fieldValue;
      } else if (fieldValue instanceof ObjectValue) {
        return ((ObjectValue) fieldValue).get(path.popFirst());
      } else {
        return null;
      }
    } else {
      return super.get(path);
    }
  }

  private MutatedObjectValue setChild(String childName, FieldValue value) {
    return new MutatedObjectValue(this.internalValue, overlays.insert(childName, value));
  }

  @Override
  public Iterator<Map.Entry<String, FieldValue>> iterator() {
    return new Iterator<Map.Entry<String, FieldValue>>() {
      Iterator<Map.Entry<String, Value>> valueIterator = new SortedMapValueIterator(internalValue);
      @Nullable Map.Entry<String, Value> valuePeek = null;
      Iterator<Map.Entry<String, FieldValue>> overlayIterator = overlays.iterator();
      @Nullable Map.Entry<String, FieldValue> overlayPeek = null;

      private void peek() {
        if (valuePeek == null && valueIterator.hasNext()) {
          valuePeek = valueIterator.next();
        }

        if (overlayPeek == null && overlayIterator.hasNext()) {
          overlayPeek = overlayIterator.next();
        }

        if (valuePeek != null && overlayPeek != null && overlayPeek.getValue() == null) {
          if (valuePeek.getKey().equals(overlayPeek.getKey())) {
            valuePeek = null;
            overlayPeek = null;
            peek();
          } else if (overlayPeek.getKey().compareTo(valuePeek.getKey()) < 0) {
            overlayPeek = null;
            peek();
          }
        } else if (overlayPeek != null && overlayPeek.getValue() == null) {
          overlayPeek = null;
          peek();
        }
      }

      @Override
      public boolean hasNext() {
        peek();
        return valuePeek != null || overlayPeek != null;
      }

      @Override
      public Map.Entry<String, FieldValue> next() {
        peek();

        if (valuePeek != null && overlayPeek != null) {
          int keyCompare = valuePeek.getKey().compareTo(overlayPeek.getKey());

          Map.Entry<String, FieldValue> result;
          if (keyCompare < 0) {
            result = createMapEntry(valuePeek.getKey(), valuePeek.getValue());
            valuePeek = null;
          } else if (keyCompare == 0) {
            result = overlayPeek;
            overlayPeek = null;
            valuePeek = null;
          } else {
            result = overlayPeek;
            overlayPeek = null;
          }
          return result;
        } else if (valuePeek != null) {
          Map.Entry<String, Value> result = valuePeek;
          valuePeek = null;
          return createMapEntry(result.getKey(), result.getValue());
        } else if (overlayPeek != null) {
          Map.Entry<String, FieldValue> result = overlayPeek;
          overlayPeek = null;
          return result;
        }

        throw new NoSuchElementException();
      }
    };
  }

  private Map.Entry<String, FieldValue> createMapEntry(String key, Value value) {
    return new Map.Entry<String, FieldValue>() {
      @Override
      public String getKey() {
        return key;
      }

      @Override
      public FieldValue getValue() {
        return PrimitiveValue.of(value);
      }

      @Override
      public FieldValue setValue(FieldValue value) {
        throw new UnsupportedOperationException("Not supported");
      }
    };
  }
}
