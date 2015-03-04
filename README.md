# java-dirty

A file-based append-only object store, using memory mapped files and flushing to disk after writes.

### Usage

Creating a store.

```
DirtyDB<Foo> store = DirtyDB.of(Foo.class).from("/path/to/file"));
```

To insert an object, use `.put()` e.g. `store.put(new Foo(1,2));`

DirtyDB does not support look-ups, replacements, or deletions. To read objects, use `.all()` which exposes a Stream.

### Performance

(TODO)

