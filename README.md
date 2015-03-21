# java-dirty

[![Build Status](https://travis-ci.org/mrwilson/java-dirty.png?branch=master)](https://travis-ci.org/mrwilson/java-dirty)

A file-based append-only object store, using memory mapped files.

## Downloading from Maven
```xml
<dependency>
  <groupId>uk.co.probablyfine</groupId>
  <artifactId>java-dirty</artifactId>
  <version>1.2</version>
</dependency>
```
## Usage

### Creating a store.
```java
Store<Foo> store = Store.of(Foo.class).from("/path/to/file");
```
### Inserting an object
```java
store.put(new Foo(1,2));
```
### Iterating over all objects in the store
```java
store.all().forEach(System.out::println);
```
### Iterate over objects, most recent first
```java
store.reverse().forEach(System.out::println);
```
### Access an index directly
```java
store.get(1234);
```
### Reset the entire store
```java
store.reset(); // Reset position to 0, overwriting old entries
```
### Observe each write
```java
store.observeWrites((object, index) ->
  System.out.println("Stored "+object+" at "+index);
);
```
java-dirty does not support replacements, or deletions. Both `.all()` and `.reverse()` expose a Stream<Foo>.

## Examples

### Look up most recent version of an object by index

```java
Optional<StoredObject> first = store
    .reverse()
    .filter(x -> x.indexField == valueToFind)
    .findFirst();
```

### Build an lookup index using write observers

```java
Store<StoredObject> store = Store.of(StoredObject.class).from("/some/path");

Map<Integer, Integer> index = new HashMap<>();

store.observeWrites((object, location) -> {
  index.put(object.indexField, location);
});

store.put(new StoredObject(1234,5));

store.get(index.get(1234)); // StoredObject(1234,5);
```

## Supported Fields

java-dirty will only persist primitive fields on objects. All primitive types are currently supported.

### Performance

(TODO)
