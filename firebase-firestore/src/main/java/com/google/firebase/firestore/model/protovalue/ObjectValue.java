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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.firebase.firestore.model.FieldPath;
import com.google.firebase.firestore.model.mutation.FieldMask;
import com.google.firebase.firestore.model.value.FieldValue;
import com.google.firebase.firestore.util.SortedMapValueIterator;
import com.google.firebase.firestore.util.Util;
import com.google.firestore.v1.Value;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static com.google.firebase.firestore.util.Assert.hardAssert;

public class ObjectValue extends PrimitiveValue implements Iterable<Map.Entry<String, FieldValue>> {
  private static final ObjectValue EMPTY_MAP_VALUE =
      new ObjectValue(
          com.google.firestore.v1.Value.newBuilder()
              .setMapValue(com.google.firestore.v1.MapValue.getDefaultInstance())
              .build());

  public ObjectValue(Value value) {
    super(value);
    hardAssert(isType(value, Value.ValueTypeCase.MAP_VALUE), "..");
  }

  public static ObjectValue emptyObject() {
    return EMPTY_MAP_VALUE;
  }

  @Override
  public int typeOrder() {
    return TYPE_ORDER_OBJECT;
  }

  @Nullable
  @Override
  public Object value() {
    return convertValue(internalValue);
  }

  @Override
  protected Map<String, Object> convertMap(com.google.firestore.v1.MapValue mapValue) {
    Map<String, Object> result = new HashMap<>();
    for (Map.Entry<String, Value> entry : mapValue.getFieldsMap().entrySet()) {
      result.put(entry.getKey(), convertValue(entry.getValue()));
    }
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o instanceof ObjectValue) {
      Iterator<Map.Entry<String, FieldValue>> iterator1 = this.iterator();
      Iterator<Map.Entry<String, FieldValue>> iterator2 = ((ObjectValue) o).iterator();
      while (iterator1.hasNext() && iterator2.hasNext()) {
        Map.Entry<String, FieldValue> entry1 = iterator1.next();
        Map.Entry<String, FieldValue> entry2 = iterator2.next();
        if (!entry1.getKey().equals(entry2.getKey())
            || !entry1.getValue().equals(entry2.getValue())) {
          return false;
        }
      }

      return !iterator1.hasNext() && !iterator2.hasNext();
    }

    return false;
  }

  @Override
  public int hashCode() {
    int hashCode = 0;
    for (Map.Entry<String, FieldValue> entry : this) {
      hashCode = hashCode * 31 + entry.getKey().hashCode();
      hashCode = hashCode * 31 + entry.getValue().hashCode();
    }
    return hashCode;
  }

  /** Recursively extracts the FieldPaths that are set in this ObjectValue. */
  public FieldMask getFieldMask() {
    Set<FieldPath> fields = new HashSet<>();
    for (Map.Entry<String, FieldValue> entry : this) {
      FieldPath currentPath = FieldPath.fromSingleSegment(entry.getKey());
      FieldValue value = entry.getValue();
      if (value instanceof ObjectValue) {
        FieldMask nestedMask = ((ObjectValue) value).getFieldMask();
        Set<FieldPath> nestedFields = nestedMask.getMask();
        if (nestedFields.isEmpty()) {
          // Preserve the empty map by adding it to the FieldMask.
          fields.add(currentPath);
        } else {
          // For nested and non-empty ObjectValues, add the FieldPath of the leaf nodes.
          for (FieldPath nestedPath : nestedFields) {
            fields.add(currentPath.append(nestedPath));
          }
        }
      } else {
        fields.add(currentPath);
      }
    }
    return FieldMask.fromSet(fields);
  }

  public MutatedObjectValue set(FieldPath path, FieldValue value) {
    return new MutatedObjectValue(internalValue).set(path, value);
  }

  public MutatedObjectValue delete(FieldPath path) {
    return new MutatedObjectValue(internalValue).delete(path);
  }

  public @Nullable FieldValue get(FieldPath path) {
    if (path.isEmpty()) {
      return this;
    }

    String childName = path.getFirstSegment();
    @Nullable Value value = this.internalValue.getMapValue().getFieldsMap().get(childName);
    int i;
    for (i = 1; isType(value, Value.ValueTypeCase.MAP_VALUE) && i < path.length(); ++i) {
      value = value.getMapValue().getFieldsMap().get(path.getSegment(i));
    }
    return value != null && i == path.length() ? PrimitiveValue.of(value) : null;
  }

  @Override
  public int compareTo(FieldValue o) {
    if (!(o instanceof ObjectValue)) {
      return Util.compareIntegers(typeOrder(), o.typeOrder());
    }

    Iterator<Map.Entry<String, FieldValue>> iterator1 = this.iterator();
    Iterator<Map.Entry<String, FieldValue>> iterator2 = ((ObjectValue) o).iterator();
    while (iterator1.hasNext() && iterator2.hasNext()) {
      Map.Entry<String, FieldValue> entry1 = iterator1.next();
      Map.Entry<String, FieldValue> entry2 = iterator2.next();
      int keyCompare = entry1.getKey().compareTo(entry2.getKey());
      if (keyCompare != 0) {
        return keyCompare;
      }
      int valueCompare = entry1.getValue().compareTo(entry2.getValue());
      if (valueCompare != 0) {
        return valueCompare;
      }
    }

    // Only equal if both iterators are exhausted.
    return Util.compareBooleans(iterator1.hasNext(), iterator2.hasNext());
  }

  @Override
  public Iterator<Map.Entry<String, FieldValue>> iterator() {
    return new Iterator<Map.Entry<String, FieldValue>>() {
      Iterator<Map.Entry<String, Value>> iterator = new SortedMapValueIterator(internalValue);

      @Override
      public boolean hasNext() {
        return iterator.hasNext();
      }

      @Override
      public Map.Entry<String, FieldValue> next() {
        Map.Entry<String, Value> entry = iterator.next();
        return new Map.Entry<String, FieldValue>() {
          @Override
          public String getKey() {
            return entry.getKey();
          }

          @Override
          public FieldValue getValue() {
            return PrimitiveValue.of(entry.getValue());
          }

          @Override
          public FieldValue setValue(FieldValue value) {
            return null;
          }
        };
      }
    };
  }
}
