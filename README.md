# BEM Tools plugin for InetllJ-based IDE's

Now working on Unix-like OS only.

**Warning:** it's alpha version.

##Installation:

Install [bem-tools-core](https://github.com/bem-tools/bem-tools-core/)

```
npm i -g bem-tools/bem-tools-core
npm i -g bem-tools/bem-tools-create
```

Download [BEM.jar](https://github.com/bem-tools/intellj-bem-tools/blob/master/BEM.jar) and install plugin (use "instal plugin from disk..." option)

##Using

File/New/BEM Block
```
level1/b1.{css,js} // will create b1.css and b1.js in level1/b1 folder
b1__e1.{css,js} // will create b1/__e1/b1__e1.css and b1/__e1/b1__d1.js in current folder
b1 // will create b1 with default techs from config in current folder
```

## Open Source Libs
[intellij-common lib] (https://github.com/idok/intellij-common)