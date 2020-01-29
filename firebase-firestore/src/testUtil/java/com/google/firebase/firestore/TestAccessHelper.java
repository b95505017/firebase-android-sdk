// Copyright 2018 Google LLC
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

import com.google.firebase.firestore.model.DocumentKey;
import org.mockito.Mockito;

public final class TestAccessHelper {

  /** Makes the DocumentReference constructor accessible. */
  public static DocumentReference createDocumentReference(DocumentKey documentKey) {
    // We can use null here because the tests only use this as a wrapper for documentKeys.
    return new DocumentReference(documentKey, null);
  }

  /** Makes the getKey() method accessible. */
  public static DocumentKey referenceKey(DocumentReference documentReference) {
    return documentReference.getKey();
  }

  /**
   * Install mocks for `FirebaseFirestore.getFirestoreSettings()` and
   * `FirebaseFirestore.getUserDataWriter()`, which are used in DocumentSnapshot tests.
   */
  public static void installDocumentSnapshotMocks(FirebaseFirestore firestore) {
    Mockito.when(firestore.getFirestoreSettings())
        .thenReturn(new FirebaseFirestoreSettings.Builder().build());
    Mockito.when(firestore.getUserDataWriter()).thenReturn(new UserDataWriter(firestore));
  }
}
