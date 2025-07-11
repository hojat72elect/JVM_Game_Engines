## HyperLap2D libGDX TinyVG Extension

HyperLap2D extension for libgdx runtime that adds [TinyVG](https://tinyvg.tech/) rendering support using [`gdx-tinyvg`](https://github.com/lyze237/gdx-TinyVG).

### Integration

#### Gradle
![maven-central](https://img.shields.io/maven-central/v/games.rednblack.hyperlap2d/libgdx-tinyvg-extension?color=blue&label=release)
![sonatype-nexus](https://img.shields.io/nexus/s/games.rednblack.hyperlap2d/libgdx-tinyvg-extension?label=sanapshot&server=https%3A%2F%2Foss.sonatype.org)

Extension needs to be included into your `core` project.
```groovy
dependencies {
    //See https://github.com/lyze237/gdx-TinyVG#installation
    api "com.github.lyze237:gdx-TinyVG:$gdxTinyVGVersion"
    
    api "games.rednblack.hyperlap2d:libgdx-tinyvg-extension:$h2dTinyVGExtension"
}
```

#### Maven
```xml
<dependency>
  <groupId>games.rednblack.hyperlap2d</groupId>
  <artifactId>libgdx-tinyvg-extension</artifactId>
  <version>0.1.5</version>
  <type>pom</type>
</dependency>
```

**TinyVG Runtime compatibility**

| HyperLap2D | gdx-TinyVG         |
|------------| ------------------ |
| 0.1.5      | 7a8927633e         |
| 0.1.4      | 7a8927633e         |

### License
HyperLap2D's libGDX runtime TinyVG extension is licensed under the Apache 2.0 License. You can use it free of charge, without limitations both in commercial and non-commercial projects. We love to get (non-mandatory) credit in case you release a game or app using HyperLap2D!

```
Copyright (c) 2022 Francesco Marongiu.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.