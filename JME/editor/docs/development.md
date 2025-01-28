# Developing for the SDK  

This document is intended to be a loose collection of stuff which is not formatted/complete enough to be added into the Wiki.  

### List of Links for the Nodes API
http://bits.netbeans.org/dev/javadoc/org-openide-nodes/org/openide/nodes/AbstractNode.html  
http://bits.netbeans.org/dev/javadoc/org-openide-nodes/org/openide/nodes/Node.html  
http://bits.netbeans.org/dev/javadoc/org-openide-nodes/org/openide/nodes/Sheet.html  
  
https://platform.netbeans.org/tutorials/nbm-nodesapi2.html  
http://wiki.netbeans.org/BasicUnderstandingOfTheNetBeansNodesAPI  
  
Make Nodes Drag and Droppable / Moving Up and Down  
https://blogs.oracle.com/geertjan/entry/node_cut_copy_paste_delete  
https://java.net/projects/nb-api-samples/sources/api-samples/show/versions/7.1/misc/DragDrop  
https://blogs.oracle.com/geertjan/entry/how_users_can_let_children  
https://blogs.oracle.com/geertjan/entry/how_users_can_let_children1  

Node Children:  
http://bits.netbeans.org/dev/javadoc/org-openide-nodes/org/openide/nodes/Children.html  
http://bits.netbeans.org/dev/javadoc/org-openide-nodes/org/openide/nodes/Children.Array.html  
http://bits.netbeans.org/dev/javadoc/org-openide-nodes/org/openide/nodes/Children.Keys.html  
http://bits.netbeans.org/8.0/javadoc/org-openide-nodes/org/openide/nodes/Index.html  

Why are my Nodes undeleteable? (Keys were regenerated)  
https://netbeans.org/projects/platform/lists/dev/archive/2008-07/message/256 

Random Exception when resetting keys (When the hashCode of a key changes):  
https://huionn.wordpress.com/2012/08/20/api-design-defensive-and-explanatory-error-message/  
  
https://blogs.oracle.com/geertjan/entry/lookuplistener_children_keys  

### Dark Monkey
Dark Monkey is based on Nimrod Look and Feel so see [here](http://nilogonzalez.es/nimrodlf/download-en.html) to download it's source and Subclass whatever is needed. Nimrod also comes with those Icons (which are a bit low res, though.)  
See [here](https://github.com/frohoff/jdk8u-dev-jdk/blob/master/src/macosx/classes/com/apple/laf/AquaMenuBarUI.java) for AquaMenuBarUI (Apple's Centralized Menu Bar). The Plan was to have Dark Monkey use this bar, but it doesn't work since we can't have those com.apple.laf packages as dependency atm, so Reflection would be needed.

### Own FileTypes (Syntax Highlighting)
https://platform.netbeans.org/tutorials/nbm-mfsyntax.html  