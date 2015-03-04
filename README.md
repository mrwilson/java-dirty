# java-dirty

[![Build Status](https://travis-ci.org/mrwilson/java-dirty.png?branch=master)](https://travis-ci.org/mrwilson/java-dirty)

A file-based append-only object store, using memory mapped files.

## Usage

### Creating a store.

```java
DirtyDB<Foo> store = DirtyDB.of(Foo.class).from("/path/to/file"));
```

### Inserting an object
```java
store.put(new Foo(1,2));
```
### Iterating over all objects in the store
```java
store.all().forEach(System.out::println);
```
DirtyDB does not support look-ups, replacements, or deletions. Use `.all()` which exposes a Stream.

## Supported Fields

java-dirty will only persist primitive fields on objects. All primitive types are currently supported.

### Performance

(TODO)

