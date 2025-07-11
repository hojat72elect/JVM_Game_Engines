# orx-noise

Randomness for every type of person: Perlin, uniform, value, simplex, fractal and many other types
of noise.

## Uniform random numbers

```kotlin
val sua = Double.uniform()
val sub = Double.uniform(-1.0, 1.0)

val v2ua = Vector2.uniform()
val v2ub = Vector2.uniform(-1.0, 1.0)
val v2uc = Vector2.uniform(Vector2(0.0, 0.0), Vector2(1.0, 1.0))
val v2ur = Vector2.uniformRing(0.5, 1.0)

val v3ua = Vector3.uniform()
val v3ub = Vector3.uniform(-1.0, 1.0)
val v3uc = Vector3.uniform(Vector3(0.0, 0.0, 0.0), Vector3(1.0, 1.0, 1.0))
val v3ur = Vector3.uniformRing(0.5, 1.0)

val v4ua = Vector4.uniform()
val v4ub = Vector4.uniform(-1.0, 1.0)
val v4uc = Vector4.uniform(Vector4(0.0, 0.0, 0.0, 0.0), Vector4(1.0, 1.0, 1.0, 1.0))
val v4ur = Vector4.uniformRing(0.5, 1.0)

val ringSamples = List(500) { Vector2.uniformRing() }
```

## Noise function composition

Since ORX 0.4 the orx-noise module comes with functional composition tooling that allow one to
create complex noise
functions.

```kotlin
// create an FBM version of 1D linear perlin noise
val myNoise0 = perlinLinear1D.fbm(octaves=3)
val noiseValue0 = myNoise0(431, seconds)

// create polar version of 2D simplex noise
val myNoise1 = simplex2D.withPolarInput()
val noiseValue1 = myNoise1(5509, Polar(seconds*60.0, 0.5))

// create value linear noise with squared outputs which is then billowed
val myNoise2 = valueLinear1D.mapOutput { it * it }.billow()
val noiseValue2 = myNoise2(993, seconds * 0.1)
```

## Multi-dimensional noise

These are a mostly straight port from FastNoise-Java but have a slightly different interface.

### Perlin noise

```kotlin
// -- 1d
val v0 = perlinLinear(seed, x)
val v1 = perlinQuintic(seed, x)
val v2 = perlinHermite(seed, x)

// -- 2d
val v3 = perlinLinear(seed, x, y)
val v4 = perlinQuintic(seed, x, y)
val v5 = perlinHermite(seed, x, y)

// -- 3d
val v6 = perlinLinear(seed, x, y, z)
val v7 = perlinQuintic(seed, x, y, z)
val v8 = perlinHermite(seed, x, y, z)
```

### Value noise

```kotlin
// -- 1d
val v0 = valueLinear(seed, x)
val v1 = valueQuintic(seed, x)
val v2 = valueHermite(seed, x)

// -- 2d
val v2 = valueLinear(seed, x, y)
val v3 = valueQuintic(seed, x, y)
val v4 = valueHermite(seed, x, y)

// -- 3d
val v5 = valueLinear(seed, x, y, z)
val v6 = valueQuintic(seed, x, y, z)
val v7 = valueHermite(seed, x, y ,z)
```

### Simplex noise

```kotlin
// -- 1d
val v0 = simplex(seed, x)

// -- 2d
val v1 = simplex(seed, x, y)

// -- 3d
val v2 = simplex(seed, x, y, z)

// -- 4d
val v3 = simplex(seed, x, y, z, w)
```

### Cubic noise

```kotlin
// -- 1d
val v0 = cubic(seed, x, y)
val v1 = cubicQuintic(seed, x, y)
val v2 = cubicHermite(seed, x, y)

// -- 2d
val v0 = cubic(seed, x, y)
val v1 = cubicQuintic(seed, x, y)
val v2 = cubicHermite(seed, x, y)

// -- 3d
val v3 = cubic(seed, x, y, z)
val v4 = cubicQuintic(seed, x, y, z)
val v5 = cubicHermite(seed, x, y ,z)
```

### Fractal noise

The library provides 3 functions with which fractal noise can be composed.

#### Fractal brownian motion (FBM)

```kotlin
// 1d
val v0 = fbm(seed, x, ::perlinLinear, octaves, lacunarity, gain)
val v1 = fbm(seed, x, ::simplexLinear, octaves, lacunarity, gain)
val v2 = fbm(seed, x, ::valueLinear, octaves, lacunarity, gain)

// 2d
val v3 = fbm(seed, x, y, ::perlinLinear, octaves, lacunarity, gain)
val v4 = fbm(seed, x, y, ::simplexLinear, octaves, lacunarity, gain)
val v5 = fbm(seed, x, y, ::valueLinear, octaves, lacunarity, gain)

// 3d
val v6 = fbm(seed, x, y, z, ::perlinLinear, octaves, lacunarity, gain)
val v7 = fbm(seed, x, y, z, ::simplexLinear, octaves, lacunarity, gain)
val v8 = fbm(seed, x, y, z, ::valueLinear, octaves, lacunarity, gain)
```

#### Rigid

```kotlin
// 1d
val v0 = rigid(seed, x, ::perlinLinear, octaves, lacunarity, gain)
val v1 = rigid(seed, x, ::simplexLinear, octaves, lacunarity, gain)
val v2 = rigid(seed, x, ::valueLinear, octaves, lacunarity, gain)

// 2d
val v2 = rigid(seed, x, y, ::perlinLinear, octaves, lacunarity, gain)
val v3 = rigid(seed, x, y, ::simplexLinear, octaves, lacunarity, gain)
val v4 = rigid(seed, x, y, ::valueLinear, octaves, lacunarity, gain)

// 3d
val v3 = rigid(seed, x, y, z, ::perlinLinear, octaves, lacunarity, gain)
val v4 = rigid(seed, x, y, z, ::simplexLinear, octaves, lacunarity, gain)
val v5 = rigid(seed, x, y, z, ::valueLinear, octaves, lacunarity, gain)
```

#### Billow

```kotlin
// 1d
val v0 = billow(seed, x, ::perlinLinear, octaves, lacunarity, gain)
val v1 = billow(seed, x, ::perlinLinear, octaves, lacunarity, gain)
val v2 = billow(seed, x, ::perlinLinear, octaves, lacunarity, gain)

// 2d
val v3 = billow(seed, x, y, ::perlinLinear, octaves, lacunarity, gain)
val v4 = billow(seed, x, y, ::perlinLinear, octaves, lacunarity, gain)
val v5 = billow(seed, x, y, ::perlinLinear, octaves, lacunarity, gain)

// 3d
val v6 = billow(seed, x, y, z, ::perlinLinear, octaves, lacunarity, gain)
val v7 = billow(seed, x, y, z, ::perlinLinear, octaves, lacunarity, gain)
val v8 = billow(seed, x, y, z, ::perlinLinear, octaves, lacunarity, gain)
```

<!-- __demos__ -->

## Demos

### DemoCubicNoise2D01

[source code](src/jvmDemo/kotlin/DemoCubicNoise2D01.kt)

![DemoCubicNoise2D01Kt](https://raw.githubusercontent.com/openrndr/orx/media/orx-noise/images/DemoCubicNoise2D01Kt.png)

### DemoFunctionalComposition01

[source code](src/jvmDemo/kotlin/DemoFunctionalComposition01.kt)

![DemoFunctionalComposition01Kt](https://raw.githubusercontent.com/openrndr/orx/media/orx-noise/images/DemoFunctionalComposition01Kt.png)

### DemoGradientPerturb2D

[source code](src/jvmDemo/kotlin/DemoGradientPerturb2D.kt)

![DemoGradientPerturb2DKt](https://raw.githubusercontent.com/openrndr/orx/media/orx-noise/images/DemoGradientPerturb2DKt.png)

### DemoGradientPerturb3D

[source code](src/jvmDemo/kotlin/DemoGradientPerturb3D.kt)

![DemoGradientPerturb3DKt](https://raw.githubusercontent.com/openrndr/orx/media/orx-noise/images/DemoGradientPerturb3DKt.png)

### DemoScatter01

[source code](src/jvmDemo/kotlin/DemoScatter01.kt)

![DemoScatter01Kt](https://raw.githubusercontent.com/openrndr/orx/media/orx-noise/images/DemoScatter01Kt.png)

### DemoSimplex01

[source code](src/jvmDemo/kotlin/DemoSimplex01.kt)

![DemoSimplex01Kt](https://raw.githubusercontent.com/openrndr/orx/media/orx-noise/images/DemoSimplex01Kt.png)

### DemoTriangleNoise01

[source code](src/jvmDemo/kotlin/DemoTriangleNoise01.kt)

![DemoTriangleNoise01Kt](https://raw.githubusercontent.com/openrndr/orx/media/orx-noise/images/DemoTriangleNoise01Kt.png)

### DemoValueNoise2D01

[source code](src/jvmDemo/kotlin/DemoValueNoise2D01.kt)

![DemoValueNoise2D01Kt](https://raw.githubusercontent.com/openrndr/orx/media/orx-noise/images/DemoValueNoise2D01Kt.png)

### glsl/DemoNoisesGLSLGui

[source code](src/jvmDemo/kotlin/glsl/DemoNoisesGLSLGui.kt)

![glsl-DemoNoisesGLSLGuiKt](https://raw.githubusercontent.com/openrndr/orx/media/orx-noise/images/glsl-DemoNoisesGLSLGuiKt.png)

### glsl/DemoNoisesGLSL

[source code](src/jvmDemo/kotlin/glsl/DemoNoisesGLSL.kt)

![glsl-DemoNoisesGLSLKt](https://raw.githubusercontent.com/openrndr/orx/media/orx-noise/images/glsl-DemoNoisesGLSLKt.png)

### glsl/DemoSimplexGLSL

[source code](src/jvmDemo/kotlin/glsl/DemoSimplexGLSL.kt)

![glsl-DemoSimplexGLSLKt](https://raw.githubusercontent.com/openrndr/orx/media/orx-noise/images/glsl-DemoSimplexGLSLKt.png)

### hammersley/DemoHammersley2D01

[source code](src/jvmDemo/kotlin/hammersley/DemoHammersley2D01.kt)

![hammersley-DemoHammersley2D01Kt](https://raw.githubusercontent.com/openrndr/orx/media/orx-noise/images/hammersley-DemoHammersley2D01Kt.png)

### hammersley/DemoHammersley3D01

[source code](src/jvmDemo/kotlin/hammersley/DemoHammersley3D01.kt)

![hammersley-DemoHammersley3D01Kt](https://raw.githubusercontent.com/openrndr/orx/media/orx-noise/images/hammersley-DemoHammersley3D01Kt.png)

### hammersley/DemoHammersley4D01

[source code](src/jvmDemo/kotlin/hammersley/DemoHammersley4D01.kt)

![hammersley-DemoHammersley4D01Kt](https://raw.githubusercontent.com/openrndr/orx/media/orx-noise/images/hammersley-DemoHammersley4D01Kt.png)

### hash/DemoCircleHash01

[source code](src/jvmDemo/kotlin/hash/DemoCircleHash01.kt)

![hash-DemoCircleHash01Kt](https://raw.githubusercontent.com/openrndr/orx/media/orx-noise/images/hash-DemoCircleHash01Kt.png)

### hash/DemoRectangleHash01

[source code](src/jvmDemo/kotlin/hash/DemoRectangleHash01.kt)

![hash-DemoRectangleHash01Kt](https://raw.githubusercontent.com/openrndr/orx/media/orx-noise/images/hash-DemoRectangleHash01Kt.png)

### hash/DemoUHash01

[source code](src/jvmDemo/kotlin/hash/DemoUHash01.kt)

![hash-DemoUHash01Kt](https://raw.githubusercontent.com/openrndr/orx/media/orx-noise/images/hash-DemoUHash01Kt.png)

### linearrange/DemoLinearRange01

[source code](src/jvmDemo/kotlin/linearrange/DemoLinearRange01.kt)

![linearrange-DemoLinearRange01Kt](https://raw.githubusercontent.com/openrndr/orx/media/orx-noise/images/linearrange-DemoLinearRange01Kt.png)

### phrases/DemoUHashPhrase01

[source code](src/jvmDemo/kotlin/phrases/DemoUHashPhrase01.kt)

![phrases-DemoUHashPhrase01Kt](https://raw.githubusercontent.com/openrndr/orx/media/orx-noise/images/phrases-DemoUHashPhrase01Kt.png)

### rseq/DemoRseq2D01

[source code](src/jvmDemo/kotlin/rseq/DemoRseq2D01.kt)

![rseq-DemoRseq2D01Kt](https://raw.githubusercontent.com/openrndr/orx/media/orx-noise/images/rseq-DemoRseq2D01Kt.png)

### rseq/DemoRseq3D01

[source code](src/jvmDemo/kotlin/rseq/DemoRseq3D01.kt)

![rseq-DemoRseq3D01Kt](https://raw.githubusercontent.com/openrndr/orx/media/orx-noise/images/rseq-DemoRseq3D01Kt.png)

### rseq/DemoRseq4D01

[source code](src/jvmDemo/kotlin/rseq/DemoRseq4D01.kt)

![rseq-DemoRseq4D01Kt](https://raw.githubusercontent.com/openrndr/orx/media/orx-noise/images/rseq-DemoRseq4D01Kt.png)

### simplexrange/DemoSimplexRange2D01

[source code](src/jvmDemo/kotlin/simplexrange/DemoSimplexRange2D01.kt)

![simplexrange-DemoSimplexRange2D01Kt](https://raw.githubusercontent.com/openrndr/orx/media/orx-noise/images/simplexrange-DemoSimplexRange2D01Kt.png)

### simplexrange/DemoSimplexRange2D02

[source code](src/jvmDemo/kotlin/simplexrange/DemoSimplexRange2D02.kt)

![simplexrange-DemoSimplexRange2D02Kt](https://raw.githubusercontent.com/openrndr/orx/media/orx-noise/images/simplexrange-DemoSimplexRange2D02Kt.png)

### simplexrange/DemoSimplexUniform01

[source code](src/jvmDemo/kotlin/simplexrange/DemoSimplexUniform01.kt)

![simplexrange-DemoSimplexUniform01Kt](https://raw.githubusercontent.com/openrndr/orx/media/orx-noise/images/simplexrange-DemoSimplexUniform01Kt.png)

### simplexrange/DemoSimplexUniform02

[source code](src/jvmDemo/kotlin/simplexrange/DemoSimplexUniform02.kt)

![simplexrange-DemoSimplexUniform02Kt](https://raw.githubusercontent.com/openrndr/orx/media/orx-noise/images/simplexrange-DemoSimplexUniform02Kt.png)
