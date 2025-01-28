## Jila Game Engine

this is my JVM-based game engine and is still in development.

The libraries that I am currently using in this project are:

<ul>
<li>Assimp</li>
<a href="https://www.assimp.org/">Open Asset Import Library</a> is a library to load various 3d file formats into a shared, in-memory imediate format. It supports more than 40 file formats for import and a growing selection of file formats for export.
<li><a href="https://github.com/bkaradzic/bgfx">BGFX</a></li>
Cross-platform, graphics API agnostic, "Bring Your Own Engine/Framework" style rendering library.
<li><a href="https://developer.nvidia.com/cuda-zone/">CUDA</a></li>
 a parallel computing platform and programming model developed by NVIDIA for general computing on graphical processing units (GPUs). With CUDA, developers are able to dramatically speed up computing applications by harnessing the power of GPUs.
<li>EGL</li>
<a href="https://www.khronos.org/egl">EGL™</a> is an interface between Khronos rendering APIs such as OpenGL ES or OpenVG and the underlying native platform window system. It handles graphics context management, surface/buffer binding, and rendering synchronization and enables high-performance, accelerated, mixed-mode 2D and 3D rendering using other Khronos APIs. EGL also provides interop capability between Khronos to enable efficient transfer of data between APIs – for example between a video subsystem running OpenMAX AL and a GPU running OpenGL ES.
<li>FMOD</li>
<a href="https://www.fmod.com/">Fmod</a> is an end-to-end solution for adding sound and music to any games.
<li><a href="https://freetype.org/">FreeType</a></li>
A freely available software library to render fonts. It is written in C, designed to be small, efficient, highly customizable, and portable while capable of producing high-quality output (glyph images) of most vector and bitmap font formats.
<li>GLFW</li>
<a href="https://www.glfw.org/">GLFW</a> is an Open Source, multi-platform library for OpenGL, OpenGL ES and Vulkan development on the desktop. It provides a simple API for creating windows, contexts and surfaces, receiving input and events. GLFW is written in C and supports Windows, macOS, Wayland and X11.
<li>HarfBuzz</li>
<a href="https://harfbuzz.github.io/">HarfBuzz</a> is a text shaping library. Using the HarfBuzz library allows programs to convert a sequence of Unicode input into properly formatted and positioned glyph output—for any writing system and language.
<li>hwloc</li>
The <a href="https://www.open-mpi.org/projects/hwloc/">Portable Hardware Locality (hwloc)</a> software package provides a portable abstraction (across OS, versions, architectures, ...) of the hierarchical topology of modern architectures, including NUMA memory nodes (DRAM, HBM, non-volatile memory, CXL, etc.), processor packages, shared caches, cores and simultaneous multithreading. It also gathers various system attributes such as cache and memory information as well as the locality of I/O devices such as network interfaces, InfiniBand HCAs or GPUs.
<li>JAWT</li>
The new <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/awt/AWT_Native_Interface.html">Java SE AWT</a> Native Interface enables rendering libraries compiled to native code to draw directly to a Java Canvas drawing surface. This means that such libraries can be used without being converted to Java first and without significant impact on performance. An example illustrating how easy it is to use the AWT Native Interface is presented and discussed in this technical note.
<li>jemalloc</li>
<a href="https://jemalloc.net/">jemalloc</a> is a general purpose malloc(3) implementation that emphasizes fragmentation avoidance and scalable concurrency support. jemalloc first came into use as the FreeBSD libc allocator in 2005, and since then it has found its way into numerous applications that rely on its predictable behavior. In 2010 jemalloc development efforts broadened to include developer support features such as heap profiling and extensive monitoring/tuning hooks. Modern jemalloc releases continue to be integrated back into FreeBSD, and therefore versatility remains critical. Ongoing development efforts trend toward making jemalloc among the best allocators for a broad range of demanding applications, and eliminating/mitigating weaknesses that have practical repercussions for real world applications.
<li>KTX (Khronos Texture)</li>
<a href="https://www.khronos.org/ktx/">KTX (Khronos Texture)</a> is an efficient, lightweight container format for reliably distributing GPU textures to diverse platforms and applications. The contents of a KTX file can range from a simple base-level 2D texture to a cubemap array texture with mipmaps. KTX files hold all the parameters needed for efficient texture loading into 3D APIs such as OpenGL® and Vulkan®, including access to individual mipmap levels
<li>libdivide</li>
<a href="https://libdivide.com/">libdivide</a> allows you to replace expensive integer divides with comparatively cheap multiplication and bitshifts. Compilers usually do this, but only when the divisor is known at compile time. libdivide allows you to take advantage of it at runtime. The result is that integer division can become faster - a lot faster.
Furthermore, libdivide allows you to divide SIMD vectors by runtime constants, which is especially nice because SIMD typically lacks integer division. libdivide is free and open source with a permissive license. It is packed as a single header-only library, with both a C and C++ API.
<li><a href="https://llvm.org/">LLVM</a></li>
The LLVM Project is a collection of modular and reusable compiler and toolchain technologies.
<li>LMDB</li>
<a href="https://www.symas.com/lmdb">Lightning Memory-Mapped Database</a> is an ultra-fast, ultra-compact, crash-proof, key-value, embedded data store. Symas LMDB is an extraordinarily fast, memory-efficient database developed for the OpenLDAP Project. With memory-mapped files, LMDB has the read performance of a pure in-memory database while retaining the persistence of standard disk-based databases.
<li>LZ4</li>
<a href="https://lz4.org/">LZ4</a> is lossless compression algorithm, providing compression speed > 500 MB/s per core (>0.15 Bytes/cycle). It features an extremely fast decoder, with speed in multiple GB/s per core (~1 Byte/cycle). A high compression derivative, called LZ4_HC, is available, trading customizable CPU time for compression ratio. LZ4 library is provided as open source software using a BSD license.
<li>Meow Hash</li>
<a href="https://github.com/cmuratori/meow_hash/">Meow hash</a> is an extremely fast level 1 hash. Please pay attention this library is not available on FreeBSD x64, Linux arm32, Linux ppc64le, and Linux riscv64. 
<li><a href="https://github.com/zeux/meshoptimizer">meshoptimizer</a></li>
It is a mesh optimization library that makes meshes smaller and faster to render.
<li>msdfgen</li>
<a href="https://github.com/Chlumsky/msdfgen">Multi-channel signed distance field generator</a>. It's a utility for generating signed distance fields from vector shapes and font glyphs, which serve as a texture representation that can be used in real-time graphics to efficiently reproduce said shapes.
<li><a href="https://github.com/memononen/nanovg">NanoVG</a></li>
Antialiased 2D vector drawing library on top of OpenGL for UI and visualizations.
<li><a href="https://github.com/memononen/nanosvg">NanoSVG</a></li>
Simple stupid SVG parser.
<li><a href="https://github.com/mlabbe/nativefiledialog">Native File Dialog</a></li>
A tiny, neat C library that portably invokes native file open and save dialogs.
<li>Nuklear</li>
<a href="https://github.com/Immediate-Mode-UI/Nuklear">Nuklear</a> is a minimal-state, immediate-mode graphical user interface toolkit written in ANSI C and licensed under public domain. It was designed as a simple embeddable user interface for application and does not have any dependencies, a default render backend or OS window/input handling but instead provides a highly modular, library-based approach, with simple input state for input and draw commands describing primitive shapes as output. So instead of providing a layered library that tries to abstract over a number of platform and render backends, it focuses only on the actual UI.
<li>ODBC</li>
<a href="https://learn.microsoft.com/en-us/sql/odbc/microsoft-open-database-connectivity-odbc?view=sql-server-ver16">Microsoft Open Database Connectivity (ODBC)</a> interface is a C programming language interface that makes it possible for applications to access data from various database management systems (DBMSs). ODBC is a low-level, high-performance interface that is designed specifically for relational data stores.

The ODBC interface allows maximum interoperability-an application can access data in diverse DBMSs through a single interface. Moreover, that application is independent of any DBMS from which it accesses data. Users of the application can add software components called drivers, which interface between an application and a specific DBMS.
<li>OpenAL</li>
<a href="https://www.openal.org/">OpenAL</a> is a cross-platform 3D audio API appropriate for use with gaming applications and many other types of audio applications.

The library models a collection of audio sources moving in a 3D space that are heard by a single listener somewhere in that space. The basic OpenAL objects are a Listener, a Source, and a Buffer. There can be a large number of Buffers, which contain audio data. Each buffer can be attached to one or more Sources, which represent points in 3D space which are emitting audio. There is always one Listener object (per audio context), which represents the position where the sources are heard -- rendering is done from the perspective of the Listener.
<li><a href="https://www.khronos.org/opencl/">OpenCL</a></li>
Open Standard for Parallel Programming of Heterogeneous Systems
OpenCL™ (Open Computing Language) is an open, royalty-free standard for cross-platform, parallel programming of diverse accelerators found in supercomputers, cloud servers, personal computers, mobile devices and embedded platforms. OpenCL greatly improves the speed and responsiveness of a wide spectrum of applications in numerous market categories including professional creative tools, scientific and medical software, vision processing, and neural network training and inferencing.
<li>OpenGL</li>
<a href="https://www.opengl.org/">OpenGL</a> is the most widely adopted 2D and 3D graphics API in the industry, bringing thousands of applications to a wide variety of platforms.
<li>OpenGL ES</li>
<a href="https://www.khronos.org/opengles/">OpenGL® ES</a> is a royalty-free, cross-platform API for rendering advanced 2D and 3D graphics on embedded and mobile systems - including consoles, phones, appliances and vehicles. OpenGL ES is suitable for low-power devices, and provides a flexible and powerful interface between software and graphics acceleration hardware.
<li>OpenVR</li>
<a href="https://github.com/ValveSoftware/openvr">OpenVR</a> is an API and runtime that allows access to VR hardware from multiple vendors without requiring that applications have specific knowledge of the hardware they are targeting. This repository is an SDK that contains the API and samples. The runtime is under SteamVR in Tools on Steam.
<li>OpenXR</li>
<a href="https://www.khronos.org/openxr/">OpenXR</a> is a royalty-free, open standard that provides a common set of APIs for developing XR applications that run across a wide range of AR and VR devices. This reduces the time and cost required for developers to adapt solutions to individual XR platforms while also creating a larger market of easily supported applications for device manufacturers that adopt OpenXR.
<li>Opus</li>
<a href="https://opus-codec.org/">Opus</a> is a totally open, royalty-free, highly versatile audio codec. Opus is unmatched for interactive speech and music transmission over the Internet, but is also intended for storage and streaming applications. It is standardized by the Internet Engineering Task Force (IETF) as RFC 6716 which incorporated technology from Skype’s SILK codec and Xiph.Org’s CELT codec.
<li><a href="https://developers.meta.com/horizon/">OVR</a></li>
The API for Oculus SDK. 
<li><a href="https://prideout.net/shapes">par_shapes</a></li>
Generate parametric surfaces and other simple shapes.
<li><a href="https://github.com/Celtoys/Remotery">Remotery</a></li>
Single C file, Realtime CPU/GPU Profiler with Remote Web Viewer.
<li><a href="https://github.com/mjansson/rpmalloc">rpmalloc</a></li>
Public domain cross platform lock free thread caching 16-byte aligned memory allocator implemented in C.
<li><a href="https://github.com/google/shaderc">shaderc</a></li>
About
A collection of tools, libraries, and tests for Vulkan shader compilation.
<li>SPIRV-Cross</li>
<a href="https://github.com/KhronosGroup/SPIRV-Cross">SPIRV-Cross</a> is a practical tool and library for performing reflection on SPIR-V and disassembling SPIR-V back to high level languages.
<li><a href="https://www.intel.com/content/www/us/en/docs/intrinsics-guide/index.html">SSE</a></li>
Simple SSE intrinsics.
<li>STB</li>
<a href="https://github.com/nothings/stb">STB</a> is a single-file public domain libraries for C/C++
<li>Tiny OpenEXR</li>
<a href="https://github.com/syoyo/tinyexr">Tiny OpenEXR</a> is an image loader/saver library.
<li><a href="https://sourceforge.net/projects/tinyfiledialogs/files/">Tiny File Dialogs</a></li>
Provides basic modal dialogs.
<li>AMD Tootle</li>
<a href="https://github.com/GPUOpen-Archive/amd_tootle">AMD Tootle</a> is a tool for Triangle Order Optimization.
<li><a href="https://github.com/GPUOpen-LibrariesAndSDKs/VulkanMemoryAllocator">Vulkan Memory Allocator</a></li>
Easy to integrate Vulkan memory allocation library
<li><a href="https://www.vulkan.org/">Vulkan</a></li>
A new generation of graphics and compute API which provides high-efficiency, and cross-platform access to modern GPUs used in a wide variety of devices from PCs and Consoles to mobile phones and embedded devices.  
<li>xxHash</li>
<a href="https://xxhash.com/">xxHash</a> is an extremely fast non-cryptographic hash algorithm, working at RAM speed limit. It is proposed in four flavors (XXH32, XXH64, XXH3_64bits and XXH3_128bits). The latest variant, XXH3, offers improved performance across the board, especially on small data.
<li><a href="https://github.com/facebook/yoga">Yoga</a></li>
An open-source, cross-platform layout library that implements Flexbox.
<li>Zstandard</li>
<a href="https://facebook.github.io/zstd/">Zstandard</a> is a fast compression algorithm, providing high compression ratios. It also offers a special mode for small data, called dictionary compression. The reference library offers a very wide range of speed / compression trade-off, and is backed by an extremely fast decoder (see benchmarks below). Zstandard library is provided as open source software using a BSD license. Its format is stable and published as IETF RFC 8878.
<li><a href="https://joml-ci.github.io/JOML/">JOML</a></li>
A Java math library for OpenGL rendering calculations | use it on: Desktop / Android / GWT
<li><a href="https://github.com/LWJGLX/lwjgl3-awt">AWT</a></li>
AWT support for LWJGL3.
<li>SteamWorks4J</li>
The <a href="https://code-disaster.github.io/steamworks4j/">steamworks4j</a> library allows Java applications to access the Steamworks C++ API. It can be easily integrated with other frameworks, such as libGDX, LWJGL or Slick2D.
</ul>