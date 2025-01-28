## SDK Plugins
Our SDK consists of the Netbeans IDE and additional "Netbeans Modules" (nbm) bringing the jme specific tools.
We call those Modules a `Plugin` whenever it is _not_ part of _this_ repository (not part of the standard distribution).
This means every code which is optional is a plugin. In fact some `core modules` have been moved out to be a plugin for various reasons.

### How can I develop a Plugin?
As has been said, developing a plugin is essentially developing a netbeans module.
For this open the SDK Project with Netbeans (if you previously built the SDK, there is the correct version of netbeans in the `netbeans` folder).
Then add a module using your desired package and module names, just follow the Wizard.
You can now depend on SDK or Netbeans API Modules and use the Wizards to realize your plugin's functionality.
Search the internet for "Netbeans Platform Modules" or "Netbeans Platform API" when you are stuck.

### How do I build a Plugin?
Building a plugin basically works by invoking `ant nbm` on the folder and taking the resulting nbm file.
Since this requires an active SDK build and can become more complex (e.g. when signing), look at https://github.com/jMonkeyEngine/sdk-update-center/tree/plugin-ci-templates for CI Templates.
Currently this is realized using Github Actions. All you need to do is copying the related file and changing the package names.

### How do I distribute a Plugin?
The most simple way of distributing a plugin is to make users install your nbm.
This means they have to download the file (most likely from your Github Releases section) and load the file using their SDK.

We use the [SDK Update Center](https://github.com/jMonkeyEngine/sdk-update-center) to distribute our Plugins.
This is where you want to apply for access as well, because your plugin will be available to every SDK User without further configuration.  
Due to security reasons, applications are rather strict and can be rejected. Please understand that we have to be strict in order to prevent abuse.
Click [here](https://github.com/jMonkeyEngine/sdk-update-center/tree/plugin-application) for more details.

If your Plugins aren't SDK related (e.g. you extend the SDK to be an Editor for your game), you can also host your own Update Center.
Fork the update-center repository for this and either distribute your code as modules (fork the SDK, add the modules and use the update-center for nightlies)
or distribute your code as plugins (see our plugin branches for that).
