## What does this folder do?
This is pretty simple:  
Since our SDK is based on Netbeans we download the so-called Netbeans Harness from official sources.  
This Harness is basically the skeleton of our SDK.  

In order to change the icon on Windows Systems we need to change files of said Harness.  
Since this harness could be re-downloaded any time we needed the `overrideHarness` build functionality.  
It simply overwrites harness files _each build_ (When necessary).  
  
In this folder you'll find our patched .exe files which will replace the harness files.  
The downside is that you have to manually change the icon (or whatever manual editing is required) each time a new netbeans version is used as sdk base. See the `resources/` folder for more information on that.  
> Note: Fortunately it doesn't have to be that the .exe files are changed aswell, so just look into it.

To prevent patching an older version onto a newer netbeans we have the override.properties,  
They define an `beforeHash`and an `afterHash` for each file.  
Before is the official netbeans source and after is our own patch file (we use this to detect an already patched file/unauthorized changes to _THIS_ folder.)

How do I generate such a hash? Those are SHA-256 hashes and either you make the build fail and see what gradle outputs or you issue `shasum -a 256 harness-override/*.exe` to print them out.