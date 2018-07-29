# Act Document Processor

The application process ActFramework document markdown source and generate the final PDF book.

1. Process tag substitution
    In order to hide TeX instructions from the markdown, so we have created special tags with each one represent a specific TeX instruction. Before we generating the PDF book, we need to substitute the tags with TeX instructions.
2. Concatenate multiple markdown source files into single file

## Tags substitution

### Meta block

`<meta-block>` mapped to the following code

```
---
header-includes:
- \usepackage{draftwatermark}
output: 
pdf_document: 
keep_tex: yes
---
```

### Other tags

* `<wip>` mapped to `\SetWatermarkText{WIP}`
* `<new-page>` mapped to `\newpage`
