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

package com.google.firebase.firestore.model.value;

import com.google.firestore.v1.Value;

/** A wrapper for float/double values in Firestore. */
public class DoubleValue extends NumberValue {
  public static final DoubleValue NaN =
      new DoubleValue(Value.newBuilder().setDoubleValue(Double.NaN).build());

  DoubleValue(Value value) {
    super(value);
  }

  public double getDoubleValue() {
    return internalValue.getDoubleValue();
  }

  public static DoubleValue valueOf(Double val) {
    if (Double.isNaN(val)) {
      return NaN;
    } else {
      return new DoubleValue(Value.newBuilder().setDoubleValue(val).build());
    }
  }
}
