# Libraries
The Libraries of the SDK enable the user to easily manage the engine dependencies. For this there are four (!) modules in place. This design seems a bit complicated, so maybe a refactoring might be good in the long run.  
To further complicate the process, netbeans is ant and xml centric however the `project files` are dynamically generated using gradle (because the sdk gradually moved to gradle).

## The Modules
There are four modules in place: First there are the `-baselibs` and `-libraries` variants where the former are the actual engine libraries as you would specify them as dependencies and the latter are the external dependencies (i.e. dependencies needed by the engine. NiftyGUI has some dependencies, JBullet has some dependencies etc and those are in `-libraries`).  

This means `jme3-core-baselibs` and `jme3-core-libraries` are the modules which actually store the binary data (i.e. the jar files in them).
It seems to me, that those are only temporary since the other modules (`jme3-project-baselibs` and `jme3-project-libraries`) also store the jars in their release folders. Anyhow those are the modules you want to influence, their `src/com/jme3/gde/project/baselibs/*.xml` files are what define the actual Libraries.  

If you have a look at the `build.gradle` file, you can see the process of building those xmls. The system is clever: You specify artifacts/dependencies and then the built xml automatically contains the dependencies specified by that dependency. Take `jme3-jbullet` for example:  
The build.gradle for `jme3-jbullet` contains:  
```
dependencies {
    compile ('java3d:vecmath:1.3.1')
    compile files('../lib/jbullet.jar', '../lib/stack-alloc.jar')
    compile project(':jme3-core')
    compile project(':jme3-terrain')
}
```

This leads to:  
```xml
<volume>
    <type>classpath</type>
    <resource>jar:nbinst://com.jme3.gde.project.baselibs/libs/jme3-jbullet-3.1.0-stable.jar!/</resource>
    <resource>jar:nbinst://com.jme3.gde.project.libraries/libs/vecmath-1.3.1.jar!/</resource>
  </volume>
  <volume>
    <type>src</type>
    <resource>jar:nbinst://com.jme3.gde.project.baselibs/libs/jme3-jbullet-3.1.0-stable-sources.jar!/</resource>
  </volume>
  <volume>
    <type>javadoc</type>
    <resource>jar:nbinst://com.jme3.gde.project.baselibs/libs/jme3-jbullet-3.1.0-stable-javadoc.jar!/</resource>
  </volume>
```

Here you unfortunately see that the `files()` dependency could not be picked up, the reason seems to be from within gradle, at least it does not appear in `jme3-jbullet`'s dependency list. That is why there was a needed workaround in the xml generation...

## Flaws / TODOs
Apart from the automatic generation failing sometimes we have another flaw which is related to the design:  
Many libraries depend on eachother like nearly all libraries depend on core.
Now to not have 10 versions of `jme3-core` inside the libraries which might even cause the build to fail, we simply discard those inner-jme dependencies and force the user to manage them appropriately.  
The Question here is if that is the right approach? For `jme3_xbuf` it lead to problems because things like `jme3_xbuf_loader` and `jme3_xbuf_physics_loader` aren't seperate artifacts and will be bundled into one library. Thats why there is a workaround for Xbuf.  

## Corelibs vs Optlibs
It seems that when the sdk itself depends on something, it has to be in corelibs, optlibs only seems to be for the user of the sdk.
