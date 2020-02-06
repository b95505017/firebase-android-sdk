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

package com.google.firebase.firestore;

import static com.google.firebase.firestore.util.Assert.fail;

import androidx.annotation.RestrictTo;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.model.DatabaseId;
import com.google.firebase.firestore.model.DocumentKey;
import com.google.firebase.firestore.model.value.ServerTimestampValue;
import com.google.firebase.firestore.util.Logger;
import com.google.firestore.v1.ArrayValue;
import com.google.firestore.v1.Value;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Converts Firestore's internal types to the Java API types that we expose to the user.
 *
 * @hide
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class UserDataWriter {
  private final FirebaseFirestore firestore;
  private final boolean timestampsInSnapshots;
  private final DocumentSnapshot.ServerTimestampBehavior serverTimestampBehavior;

  UserDataWriter(
      FirebaseFirestore firestore,
      boolean timestampsInSnapshots,
      DocumentSnapshot.ServerTimestampBehavior serverTimestampBehavior) {
    this.firestore = firestore;
    this.timestampsInSnapshots = timestampsInSnapshots;
    this.serverTimestampBehavior = serverTimestampBehavior;
  }

  Object convertValue(Value value) {
    switch (value.getValueTypeCase()) {
      case MAP_VALUE:
        if (ServerTimestampValue.isServerTimestamp(value)) {
          return convertServerTimestamp(ServerTimestampValue.valueOf(value));
        }
        return convertObject(value.getMapValue().getFieldsMap());
      case ARRAY_VALUE:
        return convertArray(value.getArrayValue());
      case REFERENCE_VALUE:
        return convertReference(value);
      case TIMESTAMP_VALUE:
        return convertTimestamp(value.getTimestampValue());
      case NULL_VALUE:
        return null;
      case BOOLEAN_VALUE:
        return value.getBooleanValue();
      case INTEGER_VALUE:
        return value.getIntegerValue();
      case DOUBLE_VALUE:
        return value.getDoubleValue();
      case STRING_VALUE:
        return value.getStringValue();
      case BYTES_VALUE:
        return Blob.fromByteString(value.getBytesValue());
      case GEO_POINT_VALUE:
        return new GeoPoint(
            value.getGeoPointValue().getLatitude(), value.getGeoPointValue().getLongitude());
      default:
        throw fail("Unknown value type: " + value.getValueTypeCase());
    }
  }

  Map<String, Object> convertObject(Map<String, Value> mapValue) {
    Map<String, Object> result = new HashMap<>();
    for (Map.Entry<String, Value> entry : mapValue.entrySet()) {
      result.put(entry.getKey(), convertValue(entry.getValue()));
    }
    return result;
  }

  private Object convertServerTimestamp(ServerTimestampValue value) {
    switch (serverTimestampBehavior) {
      case PREVIOUS:
        return value.getPreviousValue() == null ? null : convertValue(value.getPreviousValue());
      case ESTIMATE:
        return !timestampsInSnapshots
            ? value.getLocalWriteTime().toDate()
            : value.getLocalWriteTime();
      default:
        return null;
    }
  }

  private Object convertTimestamp(com.google.protobuf.Timestamp value) {
    Timestamp timestamp = new Timestamp(value.getSeconds(), value.getNanos());
    if (timestampsInSnapshots) {
      return timestamp;
    } else {
      return timestamp.toDate();
    }
  }

  private List<Object> convertArray(ArrayValue arrayValue) {
    ArrayList<Object> result = new ArrayList<>(arrayValue.getValuesCount());
    for (Value v : arrayValue.getValuesList()) {
      result.add(convertValue(v));
    }
    return result;
  }

  private Object convertReference(Value value) {
    DatabaseId refDatabase = DatabaseId.fromName(value.getReferenceValue());
    DocumentKey key = DocumentKey.fromName(value.getReferenceValue());
    DatabaseId database = firestore.getDatabaseId();
    if (!refDatabase.equals(database)) {
      // TODO: Somehow support foreign references.
      Logger.warn(
          "DocumentSnapshot",
          "Document %s contains a document reference within a different database "
              + "(%s/%s) which is not supported. It will be treated as a reference in "
              + "the current database (%s/%s) instead.",
          key.getPath(),
          refDatabase.getProjectId(),
          refDatabase.getDatabaseId(),
          database.getProjectId(),
          database.getDatabaseId());
    }
    return new DocumentReference(key, firestore);
  }
}
