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

package com.google.firebase.firestore.model;

import static com.google.firebase.firestore.testutil.TestUtil.blob;
import static com.google.firebase.firestore.testutil.TestUtil.dbId;
import static com.google.firebase.firestore.testutil.TestUtil.field;
import static com.google.firebase.firestore.testutil.TestUtil.fieldMask;
import static com.google.firebase.firestore.testutil.TestUtil.key;
import static com.google.firebase.firestore.testutil.TestUtil.map;
import static com.google.firebase.firestore.testutil.TestUtil.ref;
import static com.google.firebase.firestore.testutil.TestUtil.valueOf;
import static com.google.firebase.firestore.testutil.TestUtil.wrap;
import static com.google.firebase.firestore.testutil.TestUtil.wrapObject;
import static com.google.firebase.firestore.testutil.TestUtil.wrapRef;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

import com.google.common.testing.EqualsTester;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.model.mutation.FieldMask;
import com.google.firebase.firestore.model.value.ObjectValue;
import com.google.firebase.firestore.model.value.ProtoValues;
import com.google.firebase.firestore.model.value.ServerTimestampValue;
import com.google.firebase.firestore.testutil.ComparatorTester;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class FieldValueTest {
//  private final Date date1;
//  private final Date date2;
//
//  public FieldValueTest() {
//    // Create a couple date objects for use in tests.
//    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
//    calendar.set(2016, 5, 20, 10, 20, 0);
//    date1 = calendar.getTime();
//
//    calendar.set(2016, 10, 21, 15, 32, 0);
//    date2 = calendar.getTime();
//  }
//
//  @Test
//  public void testExtractsFields() {
//    ObjectValue obj = wrapObject("foo", map("a", 1, "b", true, "c", "string"));
//    assertTrue(ProtoValues.isMapValue(obj.get(field("foo"))));
//    assertEquals(valueOf(1), obj.get(field("foo.a")));
//    assertEquals(valueOf(true), obj.get(field("foo.b")));
//    assertEquals(valueOf("string"), obj.get(field("foo.c")));
//
//    assertNull(obj.get(field("foo.a.b")));
//    assertNull(obj.get(field("bar")));
//    assertNull(obj.get(field("bar.a")));
//  }
//
//  @Test
//  public void testExtractsFieldMask() {
//    FieldValue val =
//        wrapObject(
//            "a",
//            "b",
//            "map",
//            map("a", 1, "b", true, "c", "string", "nested", map("d", "e")),
//            "emptymap",
//            map());
//    assertTrue(val instanceof ObjectValue);
//    FieldMask mask = ((ObjectValue) val).getFieldMask();
//    assertEquals(fieldMask("a", "map.a", "map.b", "map.c", "map.nested.d", "emptymap"), mask);
//  }
//
//  @Test
//  public void testOverwritesExistingFields() {
//    ObjectValue old = wrapObject("a", "old");
//    ObjectValue mod = setField(old, "a", wrap("mod"));
//    assertNotEquals(old, mod);
//    assertEquals(wrapObject("a", "old"), old);
//    assertEquals(wrapObject("a", "mod"), mod);
//  }
//
//  @Test
//  public void testAddsNewFields() {
//    ObjectValue empty = ObjectValue.emptyObject();
//    ObjectValue mod = setField(empty, "a", wrap("mod"));
//    assertEquals(wrapObject(), empty);
//    assertEquals(wrapObject("a", "mod"), mod);
//
//    ObjectValue old = mod;
//    mod = setField(old, "b", wrap(1));
//    assertEquals(wrapObject("a", "mod"), old);
//    assertEquals(wrapObject("a", "mod", "b", 1), mod);
//  }
//
//  @Test
//  public void testAddsMultipleNewFields() {
//    ObjectValue object = ObjectValue.emptyObject();
//    object = setField(object, "a", wrap("a"));
//    object = object.toBuilder().set(field("b"), valueOf("b")).set(field("c"), valueOf("c")).build();
//
//    assertEquals(wrapObject("a", "a", "b", "b", "c", "c"), object);
//  }
//
//  @Test
//  public void testImplicitlyCreatesObjects() {
//    ObjectValue old = wrapObject("a", "old");
//    ObjectValue mod = setField(old, "b.c.d", wrap("mod"));
//
//    assertNotEquals(old, mod);
//    assertEquals(wrapObject("a", "old"), old);
//    assertEquals(wrapObject("a", "old", "b", map("c", map("d", "mod"))), mod);
//  }
//
//  @Test
//  public void testCanOverwritePrimitivesWithObjects() {
//    ObjectValue old = wrapObject("a", map("b", "old"));
//    ObjectValue mod = setField(old, "a", map("b", "mod"));
//    assertNotEquals(old, mod);
//    assertEquals(wrapObject("a", map("b", "old")), old);
//    assertEquals(wrapObject("a", map("b", "mod")), mod);
//  }
//
//  @Test
//  public void testAddsToNestedObjects() {
//    ObjectValue old = wrapObject("a", map("b", "old"));
//    ObjectValue mod = setField(old, "a.c", wrap("mod"));
//    assertNotEquals(old, mod);
//    assertEquals(wrapObject("a", map("b", "old")), old);
//    assertEquals(wrapObject("a", map("b", "old", "c", "mod")), mod);
//  }
//
//  @Test
//  public void testDeletesKey() {
//    ObjectValue old = wrapObject("a", 1, "b", 2);
//    ObjectValue mod = deleteField(old, "a");
//
//    assertNotEquals(old, mod);
//    assertEquals(wrapObject("a", 1, "b", 2), old);
//    assertEquals(wrapObject("b", 2), mod);
//
//    ObjectValue empty = deleteField(mod, "b");
//    assertNotEquals(mod, empty);
//    assertEquals(wrapObject("b", 2), mod);
//    assertEquals(ObjectValue.emptyObject(), empty);
//  }
//
//  @Test
//  public void testDeletesHandleMissingKeys() {
//    ObjectValue old = wrapObject("a", map("b", 1, "c", 2));
//    ObjectValue mod = deleteField(old, "b");
//    assertEquals(mod, old);
//    assertEquals(wrapObject("a", map("b", 1, "c", 2)), mod);
//
//    mod = deleteField(old, "a.d");
//    assertEquals(old, mod);
//    assertEquals(wrapObject("a", map("b", 1, "c", 2)), mod);
//
//    mod = deleteField(old, "a.b.c");
//    assertEquals(old, mod);
//    assertEquals(wrapObject("a", map("b", 1, "c", 2)), mod);
//  }
//
//  @Test
//  public void testDeletesNestedKeys() {
//    Map<String, Object> orig = map("a", map("b", 1, "c", map("d", 2, "e", 3)));
//    ObjectValue old = wrapObject(orig);
//    ObjectValue mod = deleteField(old, "a.c.d");
//
//    assertNotEquals(mod, old);
//    assertEquals(wrapObject(orig), old);
//
//    Map<String, Object> second = map("a", map("b", 1, "c", map("e", 3)));
//    assertEquals(wrapObject(second), mod);
//
//    old = mod;
//    mod = deleteField(old, "a.c");
//
//    assertNotEquals(old, mod);
//    assertEquals(wrapObject(second), old);
//
//    Map<String, Object> third = map("a", map("b", 1));
//    assertEquals(wrapObject(third), mod);
//
//    old = mod;
//    mod = deleteField(old, "a");
//
//    assertNotEquals(old, mod);
//    assertEquals(wrapObject(third), old);
//    assertEquals(ObjectValue.emptyObject(), mod);
//  }
//
//  @Test
//  public void testDeletesMultipleNewFields() {
//    ObjectValue object = wrapObject("a", "a", "b", "b", "c", "c");
//    object = object.toBuilder().delete(field("a")).build();
//    object = object.toBuilder().delete(field("b")).delete(field("c")).build();
//
//    assertEquals(ObjectValue.emptyObject(), object);
//  }
//
//  @Test
//  public void testValueEquality() {
//    GeoPoint geoPoint1 = new GeoPoint(1, 0);
//    GeoPoint geoPoint2 = new GeoPoint(0, 2);
//    Timestamp timestamp1 = new Timestamp(date1);
//    Timestamp timestamp2 = new Timestamp(date2);
//    new EqualsTester()
//        .addEqualityGroup(wrap(true), new FieldValue(valueOf(true)))
//        .addEqualityGroup(wrap(false), new FieldValue(valueOf(false)))
//        .addEqualityGroup(wrap(null), new FieldValue(ProtoValues.NULL_VALUE))
//        .addEqualityGroup(
//            wrap(0.0 / 0.0),
//            wrap(Double.longBitsToDouble(0x7ff8000000000000L)),
//            new FieldValue(ProtoValues.NAN_VALUE))
//        // -0.0 and 0.0 compareTo the same but are not equal.
//        .addEqualityGroup(wrap(-0.0))
//        .addEqualityGroup(wrap(0.0))
//        .addEqualityGroup(wrap(1), new FieldValue(valueOf(1)))
//        // Doubles and Longs aren't equal.
//        .addEqualityGroup(wrap(1.0), new FieldValue(valueOf(1.0)))
//        .addEqualityGroup(wrap(1.1), new FieldValue(valueOf(1.1)))
//        .addEqualityGroup(wrap(blob(0, 1, 2)), new FieldValue(valueOf(blob(0, 1, 2))))
//        .addEqualityGroup(wrap(blob(0, 1)))
//        .addEqualityGroup(wrap("string"), new FieldValue(valueOf("string")))
//        .addEqualityGroup(wrap("strin"))
//        // latin small letter e + combining acute accent
//        .addEqualityGroup(wrap("e\u0301b"))
//        // latin small letter e with acute accent
//        .addEqualityGroup(wrap("\u00e9a"))
//        .addEqualityGroup(wrap(date1), wrap(timestamp1))
//        .addEqualityGroup(wrap(timestamp2))
//        // NOTE: ServerTimestampValues can't be parsed via wrap().
//        .addEqualityGroup(
//            ServerTimestampValue.valueOf(new Timestamp(date1), null),
//            ServerTimestampValue.valueOf(new Timestamp(date1), null))
//        .addEqualityGroup(ServerTimestampValue.valueOf(new Timestamp(date2), null))
//        .addEqualityGroup(wrap(geoPoint1), wrap(new GeoPoint(1, 0)))
//        .addEqualityGroup(wrap(geoPoint2))
//        .addEqualityGroup(wrap(ref("coll/doc1")), wrapRef(dbId("project"), key("coll/doc1")))
//        .addEqualityGroup(wrapRef(dbId("project", "bar"), key("coll/doc2")))
//        .addEqualityGroup(wrapRef(dbId("project", "baz"), key("coll/doc2")))
//        .addEqualityGroup(wrap(Arrays.asList("foo", "bar")), wrap(Arrays.asList("foo", "bar")))
//        .addEqualityGroup(wrap(Arrays.asList("foo", "bar", "baz")))
//        .addEqualityGroup(wrap(Arrays.asList("foo")))
//        .addEqualityGroup(wrapObject(map("bar", 1, "foo", 2)), wrapObject(map("foo", 2, "bar", 1)))
//        .addEqualityGroup(wrapObject(map("bar", 2, "foo", 1)))
//        .addEqualityGroup(wrapObject(map("bar", 1)))
//        .addEqualityGroup(wrapObject(map("foo", 1)))
//        .testEquals();
//  }
//
//  @Test
//  public void testValueOrdering() {
//    new ComparatorTester()
//        // do not test for compatibility with equals(): +0/-0 break it.
//        .permitInconsistencyWithEquals()
//
//        // null first
//        .addEqualityGroup(wrap(null))
//
//        // booleans
//        .addEqualityGroup(wrap(false))
//        .addEqualityGroup(wrap(true))
//
//        // numbers
//        .addEqualityGroup(wrap(Double.NaN))
//        .addEqualityGroup(wrap(Double.NEGATIVE_INFINITY))
//        .addEqualityGroup(wrap(-Double.MAX_VALUE))
//        .addEqualityGroup(wrap(Long.MIN_VALUE))
//        .addEqualityGroup(wrap(-1.1))
//        .addEqualityGroup(wrap(-1.0))
//        .addEqualityGroup(wrap(-Double.MIN_NORMAL))
//        .addEqualityGroup(wrap(-Double.MIN_VALUE))
//        // Zeros all compare the same.
//        .addEqualityGroup(wrap(-0.0), wrap(0.0), wrap(0L))
//        .addEqualityGroup(wrap(Double.MIN_VALUE))
//        .addEqualityGroup(wrap(Double.MIN_NORMAL))
//        .addEqualityGroup(wrap(0.1))
//        // Doubles and Longs compareTo() the same.
//        .addEqualityGroup(wrap(1.0), wrap(1L))
//        .addEqualityGroup(wrap(1.1))
//        .addEqualityGroup(wrap(Long.MAX_VALUE))
//        .addEqualityGroup(wrap(Double.MAX_VALUE))
//        .addEqualityGroup(wrap(Double.POSITIVE_INFINITY))
//
//        // dates
//        .addEqualityGroup(wrap(date1))
//        .addEqualityGroup(wrap(date2))
//
//        // server timestamps come after all concrete timestamps.
//        // NOTE: server timestamps can't be parsed with wrap().
//        .addEqualityGroup(ServerTimestampValue.valueOf(new Timestamp(date1), null))
//        .addEqualityGroup(ServerTimestampValue.valueOf(new Timestamp(date2), null))
//
//        // strings
//        .addEqualityGroup(wrap(""))
//        .addEqualityGroup(wrap("\000\ud7ff\ue000\uffff"))
//        .addEqualityGroup(wrap("(╯°□°）╯︵ ┻━┻"))
//        .addEqualityGroup(wrap("a"))
//        .addEqualityGroup(wrap("abc def"))
//        // latin small letter e + combining acute accent + latin small letter b
//        .addEqualityGroup(wrap("e\u0301b"))
//        .addEqualityGroup(wrap("æ"))
//        // latin small letter e with acute accent + latin small letter a
//        .addEqualityGroup(wrap("\u00e9a"))
//
//        // blobs
//        .addEqualityGroup(wrap(blob()))
//        .addEqualityGroup(wrap(blob(0)))
//        .addEqualityGroup(wrap(blob(0, 1, 2, 3, 4)))
//        .addEqualityGroup(wrap(blob(0, 1, 2, 4, 3)))
//        .addEqualityGroup(wrap(blob(255)))
//
//        // resource names
//        .addEqualityGroup(wrapRef(dbId("p1", "d1"), key("c1/doc1")))
//        .addEqualityGroup(wrapRef(dbId("p1", "d1"), key("c1/doc2")))
//        .addEqualityGroup(wrapRef(dbId("p1", "d1"), key("c10/doc1")))
//        .addEqualityGroup(wrapRef(dbId("p1", "d1"), key("c2/doc1")))
//        .addEqualityGroup(wrapRef(dbId("p1", "d2"), key("c1/doc1")))
//        .addEqualityGroup(wrapRef(dbId("p2", "d1"), key("c1/doc1")))
//
//        // geo points
//        .addEqualityGroup(wrap(new GeoPoint(-90, -180)))
//        .addEqualityGroup(wrap(new GeoPoint(-90, 0)))
//        .addEqualityGroup(wrap(new GeoPoint(-90, 180)))
//        .addEqualityGroup(wrap(new GeoPoint(0, -180)))
//        .addEqualityGroup(wrap(new GeoPoint(0, 0)))
//        .addEqualityGroup(wrap(new GeoPoint(0, 180)))
//        .addEqualityGroup(wrap(new GeoPoint(1, -180)))
//        .addEqualityGroup(wrap(new GeoPoint(1, 0)))
//        .addEqualityGroup(wrap(new GeoPoint(1, 180)))
//        .addEqualityGroup(wrap(new GeoPoint(90, -180)))
//        .addEqualityGroup(wrap(new GeoPoint(90, 0)))
//        .addEqualityGroup(wrap(new GeoPoint(90, 180)))
//
//        // arrays
//        .addEqualityGroup(wrap(Arrays.asList("bar")))
//        .addEqualityGroup(wrap(Arrays.asList("foo", 1)))
//        .addEqualityGroup(wrap(Arrays.asList("foo", 2)))
//        .addEqualityGroup(wrap(Arrays.asList("foo", "0")))
//
//        // objects
//        .addEqualityGroup(wrapObject(map("bar", 0)))
//        .addEqualityGroup(wrapObject(map("bar", 0, "foo", 1)))
//        .addEqualityGroup(wrapObject(map("foo", 1)))
//        .addEqualityGroup(wrapObject(map("foo", 2)))
//        .addEqualityGroup(wrapObject(map("foo", "0")))
//        .testCompare();
//  }
//
//  @Test
//  public void testCanonicalIds() {
//    assertCanonicalId(wrap(null), "null");
//    assertCanonicalId(wrap(true), "true");
//    assertCanonicalId(wrap(false), "false");
//    assertCanonicalId(wrap(1), "1");
//    assertCanonicalId(wrap(1.0), "1.0");
//    assertCanonicalId(wrap(new Timestamp(30, 1000)), "time(30,1000)");
//    assertCanonicalId(wrap("a"), "a");
//    assertCanonicalId(wrap(blob(1, 2, 3)), "010203");
//    assertCanonicalId(wrapRef(dbId("p1", "d1"), key("c1/doc1")), "c1/doc1");
//    assertCanonicalId(wrap(new GeoPoint(30, 60)), "geo(30.0,60.0)");
//    assertCanonicalId(wrap(Arrays.asList(1, 2, 3)), "[1,2,3]");
//    assertCanonicalId(wrap(map("a", 1, "b", 2, "c", "3")), "{a:1,b:2,c:3}");
//    assertCanonicalId(
//        wrap(map("a", Arrays.asList("b", map("c", new GeoPoint(30, 60))))),
//        "{a:[b,{c:geo(30.0,60.0)}]}");
//  }
//
//  @Test
//  public void testObjectCanonicalIdsIgnoreSortOrder() {
//    assertCanonicalId(wrap(map("a", 1, "b", 2, "c", "3")), "{a:1,b:2,c:3}");
//    assertCanonicalId(wrap(map("c", 3, "b", 2, "a", "1")), "{a:1,b:2,c:3}");
//  }
//
//  private void assertCanonicalId(FieldValue fieldValue, String expectedCanonicalId) {
//    assertEquals(expectedCanonicalId, ProtoValues.canonicalId(fieldValue.getProto()));
//  }
//
//  private ObjectValue setField(ObjectValue objectValue, String fieldPath, FieldValue value) {
//    return objectValue.toBuilder().set(field(fieldPath), value.getProto()).build();
//  }
//
//  private ObjectValue setField(
//          ObjectValue objectValue, String fieldPath, Map<String, Object> value) {
//    return objectValue.toBuilder().set(field(fieldPath), wrapObject(value).getProto()).build();
//  }
//
//  private ObjectValue deleteField(ObjectValue objectValue, String fieldPath) {
//    return objectValue.toBuilder().delete(field(fieldPath)).build();
//  }
}
