# Reporting Issues
This document is about Reporting Issues related to the SDK.  
Reporting Issues is generally no big deal and seeing you reading this, you're likely to overcomplicating it.  
Anyway in the following sections we'll describe some things about good Issue Reports.  

## General
Our Issues are all tracked on Github at the moment. If you see this document you're probably already there.  
At the top of the current repo there is this tab called "Issues". Click on it to open the Issues page.  
From there you'll have a really simple process:  
- Make sure it's really an issue (a fault in the system, doesn't work as expected) and not maybe you doing things wrong. If you're looking for support or are in doubt whether this is an issue, join us at the [forums](http://hub.jmonkeyengine.org) and post in the category Troubleshooting -> jmonkeyplatform. Note: The forum allows signing in with github.
- Look for already existing Issues. (You can use the search box by simply typing some keywords of your issue, e.g. `blender`)
- IF there are existing Issues, look into them. If there's detailed info being asked feel free to provide your's aswell. You can also comment to tell us that you also experience that Issue (So we can see how many people are effected and what to fix first)
- IF there are none, click on Create an Issue. Make sure to choose a good (but yet simple) title and just describe your problem.
- Here it is important that you provide us as much information as possible: What's meant with "doesn't work"? What exactly did you do what way? If there are files involved, pack them as .zip and attach them to the Issue. (If you don't it could be that we are unable to reproduce it)
- Submit the Issue and wait for further requests

That's basically it. No matter what, we will tell you what further information we need, just make sure to describe it as detailed as possible.  
See also the following parts on what information you _could_ include depending on the Issue.  

### Version Information
This is quite important for any release (as long as you are not using the latest version. DOUBLE SUPER TRIPLE CHECK!), since the SDK releases are fast paced.  
Note: The Welcome Screen doesn't tell the truth about this. It always shows the latest version available.  
You can try to rely on the window title but the dead safe way is to open the SDK's log and look for information like:  
```
Running on jMonkeyEngine 3.1-alpha3
* Branch: HEAD
* Git Hash: 0b487ee
* Build Date: 2016-03-14
LWJGL 2.9.3 context running on thread jME3 Main
* Graphics Adapter: null
[...]
OpenGL Renderer Information
* Renderer: NVIDIA GeForce GT 650M OpenGL Engine
[...]
Audio Renderer Information
* Device: Built-in Output
[...]
OpenAL EFX not available! Audio effects won't work.
```

Please also include the information on the About Screen (especially for Build Problems).

### Reporting Exceptions  
From time to time, you might get an "Exception" being written in your SDK Console after you experienced an Issue.  
It's important to not only copy the name of it but the full stacktrace (caused by xyz in line u, caught at ...).  
For that you'll enclose this in three backticks in the line above and below, like so:   

    ```
    java.lang.NullPointerException
    at com.jme3.scene.plugins.blender.meshes.MeshHelper.loadVerticesGroups(MeshHelper.java:289)
    at com.jme3.scene.plugins.blender.meshes.TemporalMesh.<init>(TemporalMesh.java:126)
    at com.jme3.scene.plugins.blender.meshes.TemporalMesh.<init>(TemporalMesh.java:100)
    at com.jme3.scene.plugins.blender.meshes.MeshHelper.toTemporalMesh(MeshHelper.java:114)
    at com.jme3.scene.plugins.blender.BlenderLoader.load(BlenderLoader.java:128)
    Caused: java.io.IOException: Unexpected importer exception occured: null
    at com.jme3.scene.plugins.blender.BlenderLoader.load(BlenderLoader.java:223)
    at com.jme3.scene.plugins.blender.BlenderLoader.load(BlenderLoader.java:88)
    at com.jme3.asset.DesktopAssetManager.loadLocatedAsset(DesktopAssetManager.java:262)
    ```

### Reporting Faults in the NetBeans Installer  
This happens when you try to install the SDK on your computer however get an undetailed error such as "Could not configurate JDK. Maybe wrong platform".  
To improve that, you can launch the installer from the commandline and hence filling a file with debug output.  
Search the net for information on how to open a commandline/cmd/terminal/console on your OS and to Change the Directory to the Download Folder.  
When you're there, you simply issue the following command on windows systems:  
`jmonkeyplatform-installer-x64.exe --verbose --output log.txt`(Note that the installer name might vary and that the TAB key will autocomplete your file name after typing jmonkey).  
Under linux it'll be:  
`./jmonkeyplatform-installer-x64.sh --verbose --output log.txt`  
  
Then you just need to upload that log.txt in addition to your error report and we can help you.  
Without that report it's very unlikely that we can!  

### Reporting Issues with any Model-Importer  
It's already written above but since this is so important: Please upload your models packed as a .zip file along with your Issue report or we will be completely *unable* to reproduce the Issue.  
If you can't do that due to licensing problems, try to create an own file/modify the file so you have another model where that issue appears.  

### Reporting Issues with the "Design" (Look and Feel)  
If you report Issues concering too big fonts or anything like that, go to the SDK's Preferences -> Appearance and tell us what LookandFeel is selected.  
Note: Also try to use DarkMonkey. It's a theme created by us and as such is the only to be consistent over platforms. All others might differ from platform to platform.  
