# java-dirty

[![Build Status](https://travis-ci.org/mrwilson/java-dirty.png?branch=master)](https://travis-ci.org/mrwilson/java-dirty)

A file-based append-only object store, using memory mapped files.

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

java-dirty does not support replacements, or deletions. Both `.all()` and `.reverse()` expose a Stream<Foo>.

## Example: Look up most recent version of an object by index

```java
Optional<StoredObject> first = store
    .reverse()
    .filter(x -> x.indexField == valueToFind)
    .findFirst();
```

## Supported Fields

java-dirty will only persist primitive fields on objects. All primitive types are currently supported.

### Performance

(TODO)