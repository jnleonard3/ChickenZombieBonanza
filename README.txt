Build Instructions:

1. Download and install the Eclipse IDE for Java Developers

2. Download and install the Android SDK with the ADT plugin for Eclipse

3. With the Android SDK, install the package 'Google APIs by Google Inc Android API 8 revision 2'

4. Launch Eclipse

5. Create a new Android Project by going to 'File > New > Other...' and in the new window that appears, go to 'Android > Android Project'

6. Under 'Contents' select 'Create project from existing source' and then 'Browse' to the 'project' directory of the source checkout, the rest of the information should fill out if you have selected the correct folder.

7. Under 'Build Target' select 'Google API Platform 2.2 Revision 8' and select 'Finish'

8. In the 'Project Explorer' of Eclipse, right click the newly created project and select 'Properties'

9. On the left hand side, select 'Java Build Path' and select the 'Libraries' tab

10. Press the 'Add JARs' button and select 'geotransform.jar' in the project directory under 'libs'

11. Press 'OK' and then 'OK' in the next dialog to commit the change 

12. In the menu bar, go to 'Preferences > Android > Build' and look for 'Custom debug keystore'

13. Press 'Browse' and browse to the 'configuration' directory of the project and select 'debug.keystore'

14. Press 'OK' and then 'Apply' to apply the changes.

15. You can now run the project with the Android Emulator, however you will not be able to play the shooting game due to limitations of the Emulator, you can also deploy the application to your Android phone now, consult your phone's documentation on how to do so.
