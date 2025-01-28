val lwjglVersion = "3.3.4"
val jomlVersion = "1.10.7"
val `joml-primitivesVersion` = "1.10.0"
val `lwjgl3-awtVersion` = "0.1.8"
val steamworks4jVersion = "1.9.0"
val `steamworks4j-serverVersion` = "1.9.0"

val lwjglNatives = Pair(
    System.getProperty("os.name")!!,
    System.getProperty("os.arch")!!
).let { (name, arch) ->
    when {
        "FreeBSD".equals(name) ->
            "natives-freebsd"

        arrayOf("Linux", "SunOS", "Unit").any { name.startsWith(it) } ->
            if (arrayOf("arm", "aarch64").any { arch.startsWith(it) })
                "natives-linux${if (arch.contains("64") || arch.startsWith("armv8")) "-arm64" else "-arm32"}"
            else if (arch.startsWith("ppc"))
                "natives-linux-ppc64le"
            else if (arch.startsWith("riscv"))
                "natives-linux-riscv64"
            else
                "natives-linux"

        arrayOf("Mac OS X", "Darwin").any { name.startsWith(it) } ->
            "natives-macos${if (arch.startsWith("aarch64")) "-arm64" else ""}"

        arrayOf("Windows").any { name.startsWith(it) } ->
            if (arch.contains("64"))
                "natives-windows${if (arch.startsWith("aarch64")) "-arm64" else ""}"
            else
                "natives-windows-x86"

        else ->
            throw Error("Unrecognized or unsupported platform. Please set \"lwjglNatives\" manually")
    }
}







plugins {
    id("java")
    kotlin("jvm")
}

group = "ca.hojat"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    // LWJGL
    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))
    implementation("org.lwjgl", "lwjgl")

    // Assimp
    implementation("org.lwjgl", "lwjgl-assimp")
    // BGFX
    implementation("org.lwjgl", "lwjgl-bgfx")
    // CUDA
    implementation("org.lwjgl", "lwjgl-cuda")
    // EGL
    implementation("org.lwjgl", "lwjgl-egl")
    // Fmod
    implementation("org.lwjgl", "lwjgl-fmod")
    // FreeType
    implementation("org.lwjgl", "lwjgl-freetype")
    // GLFW
    implementation("org.lwjgl", "lwjgl-glfw")
    // Harfbuzz
    implementation("org.lwjgl", "lwjgl-harfbuzz")
    // hwloc
    implementation("org.lwjgl", "lwjgl-hwloc")
    // JAWT
    implementation("org.lwjgl", "lwjgl-jawt")
    // Jemalloc
    implementation("org.lwjgl", "lwjgl-jemalloc")
    // KTX (Khronos Texture)
    implementation("org.lwjgl", "lwjgl-ktx")
    // Libdivide
    implementation("org.lwjgl", "lwjgl-libdivide")
    // LLVM
    implementation("org.lwjgl", "lwjgl-llvm")
    // LMDB
    implementation("org.lwjgl", "lwjgl-lmdb")
    // LZ4
    implementation("org.lwjgl", "lwjgl-lz4")
    // meow hash
    implementation("org.lwjgl", "lwjgl-meow")
    // meshoptimizer
    implementation("org.lwjgl", "lwjgl-meshoptimizer")
    // msdfgen
    implementation("org.lwjgl", "lwjgl-msdfgen")
    // NanoVG
    implementation("org.lwjgl", "lwjgl-nanovg")
    // Native File Dialog
    implementation("org.lwjgl", "lwjgl-nfd")
    // Nuklear
    implementation("org.lwjgl", "lwjgl-nuklear")
    // ODBC
    implementation("org.lwjgl", "lwjgl-odbc")
    // OpenAL
    implementation("org.lwjgl", "lwjgl-openal")
    // OpenCL
    implementation("org.lwjgl", "lwjgl-opencl")
    // OpenGL
    implementation("org.lwjgl", "lwjgl-opengl")
    // OpenGLES
    implementation("org.lwjgl", "lwjgl-opengles")
    // OpenVR
    implementation("org.lwjgl", "lwjgl-openvr")
    // OpenXR
    implementation("org.lwjgl", "lwjgl-openxr")
    // Opus
    implementation("org.lwjgl", "lwjgl-opus")
    // OVR
    implementation("org.lwjgl", "lwjgl-ovr")
    // PAR shapes
    implementation("org.lwjgl", "lwjgl-par")
    // Remotery
    implementation("org.lwjgl", "lwjgl-remotery")
    // RPMalloc
    implementation("org.lwjgl", "lwjgl-rpmalloc")
    // ShaderC
    implementation("org.lwjgl", "lwjgl-shaderc")
    // SPIRV-Cross
    implementation("org.lwjgl", "lwjgl-spvc")
    // SSE
    implementation("org.lwjgl", "lwjgl-sse")
    // stb
    implementation("org.lwjgl", "lwjgl-stb")
    // Tiny OpenEXR
    implementation("org.lwjgl", "lwjgl-tinyexr")
    // Tiny File Dialogs
    implementation("org.lwjgl", "lwjgl-tinyfd")
    // AMD Tootle
    implementation("org.lwjgl", "lwjgl-tootle")
    // Vulkan Memory Allocator
    implementation("org.lwjgl", "lwjgl-vma")
    // Vulkan
    implementation("org.lwjgl", "lwjgl-vulkan")
    // xxHash
    implementation("org.lwjgl", "lwjgl-xxhash")
    // Yoga
    implementation("org.lwjgl", "lwjgl-yoga")
    // Zstandard
    implementation("org.lwjgl", "lwjgl-zstd")

    // All the natives
    runtimeOnly("org.lwjgl", "lwjgl", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-assimp", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-bgfx", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-freetype", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-glfw", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-harfbuzz", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-hwloc", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-jemalloc", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-ktx", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-libdivide", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-llvm", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-lmdb", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-lz4", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-meow", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-meshoptimizer", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-msdfgen", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-nanovg", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-nfd", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-nuklear", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-openal", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-opengl", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-opengles", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-openvr", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-openxr", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-opus", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-ovr", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-par", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-remotery", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-rpmalloc", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-shaderc", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-spvc", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-sse", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-stb", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-tinyexr", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-tinyfd", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-tootle", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-vma", classifier = lwjglNatives)
    if (lwjglNatives == "natives-macos" || lwjglNatives == "natives-macos-arm64") runtimeOnly(
        "org.lwjgl",
        "lwjgl-vulkan",
        classifier = lwjglNatives
    )
    runtimeOnly("org.lwjgl", "lwjgl-xxhash", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-yoga", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-zstd", classifier = lwjglNatives)


    // joml
    implementation("org.joml", "joml", jomlVersion)
    implementation("org.joml", "joml-primitives", `joml-primitivesVersion`)
    // AWT
    implementation("org.lwjglx", "lwjgl3-awt", `lwjgl3-awtVersion`)
    // steamworks4j
    implementation("com.code-disaster.steamworks4j", "steamworks4j", steamworks4jVersion)
    implementation("com.code-disaster.steamworks4j", "steamworks4j-server", `steamworks4j-serverVersion`)


    // test
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // kotlin lang
    implementation(kotlin("stdlib-jdk8"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}