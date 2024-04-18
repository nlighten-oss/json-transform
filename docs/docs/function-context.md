---
sidebar_position: 5
---

# Function Context

Object functions support a special structure for providing context to nested transformers.

This feature is required to support nesting of iterating functions (using `##current`).

## Syntax

The key `context` would be added next to the function key (e.g. `$$map`) like an additional argument.
- The context transformer is a map of `string` (that begins with `##`) to any value that can be transformed from the external context.
- Then any transformer which is an argument of that function (e.g. `to` of `$$map`) will have both the regular function context (e.g. `##current`/`##index`) and the additional that was created in the `context`.
- The context keys are kept in any of the nested transformers context, meaning that any nested function will be able to access the context values that were set in any of the ancestors' contexts. 

## Example

In the following example we would want to flatten the list of `items` in each of the array's objects. 

But we would also like to keep the origin `id` that every item came from.

```mdx-code-block
<div className="examples_grid">
```
```json title="Input"
[
  { "id": 12, "items": ["a"] },
  { "id": 24, "items": ["b"] },
  { "id": 48, "items": ["d", "e"] }
]
```
```transformers title="Definition" {5-7}
{
  "$$flat": {
    "$$map":"$",
    "to": {
      "context": {
        "##container": "##current"
      },
      "$$map":"##current.items",
      "to": {
        "id": "##container.id",
        "value":"##current"
      }
    }
  }
}
```
```json title="Output"
[
  {
    "id": 12,
    "value": "a"
  },
  {
    "id": 24,
    "value": "b"
  },
  {
    "id": 48,
    "value": "d"
  },
  {
    "id": 48,
    "value": "e"
  }
]
```
```mdx-code-block
</div>
```
