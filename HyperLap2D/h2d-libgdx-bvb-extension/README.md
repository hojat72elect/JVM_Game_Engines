## HyperLap2D libGDX BVB Extension

HyperLap2D extension for libgdx runtime that adds BVB rendering support. This format allow to bound Talos VFXs to Spine Animation.

### Integration

#### Gradle
![maven-central](https://img.shields.io/maven-central/v/games.rednblack.hyperlap2d/libgdx-bvb-extension?color=blue&label=release)
![sonatype-nexus](https://img.shields.io/nexus/s/games.rednblack.hyperlap2d/libgdx-bvb-extension?label=snapshot&server=https%3A%2F%2Foss.sonatype.org)

Extension needs to be included into your `core` project.
```groovy
dependencies {
    api "com.esotericsoftware.spine:spine-libgdx:$spineVersion"
    api "games.rednblack.talos:runtime-libgdx:$talosVersion"
    api "games.rednblack.talos:bvb-libgdx:$talosVersion"
    api "games.rednblack.hyperlap2d:libgdx-bvb-extension:$h2dSpineExtension"
}
```

#### Maven
```xml
<dependency>
  <groupId>games.rednblack.editor</groupId>
  <artifactId>libgdx-bvb-extension</artifactId>
  <version>0.1.5</version>
  <type>pom</type>
</dependency>
```

**BVB Runtime compatibility**

| HyperLap2D | Spine | Talos Legacy |
|------------|-------|--------------|
| 0.1.5      | 4.2.7 | 1.5.1        |
| 0.1.4      | 4.1.0 | 1.5.0        |

### License
Spine is a commercial software distributed with its own license, in order to include Spine support in your project, please, be sure to have a valid [Spine License](https://github.com/EsotericSoftware/spine-runtimes)