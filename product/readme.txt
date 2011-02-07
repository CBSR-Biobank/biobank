Commands to build a feature and its plugins:
java -jar /home/delphine/apps/eclipse/plugins/org.eclipse.equinox.launcher_1.1.0.v20100507.jar  -application org.eclipse.ant.core.antRunner -buildfile /home/delphine/projects/biobank2/eclipse_ws/biobank2.feature.core/build.xml 

java -jar /home/delphine/apps/eclipse/plugins/org.eclipse.equinox.launcher_1.1.0.v20100507.jar  -application org.eclipse.ant.core.antRunner -buildfile /home/delphine/projects/biobank2/eclipse_ws/biobank2.feature.platform/build.xml

Command to build a product:
java -jar /home/delphine/apps/eclipse/plugins/org.eclipse.equinox.launcher_1.1.0.v20100507.jar -application org.eclipse.ant.core.antRunner -buildfile /home/delphine/apps/eclipse/plugins/org.eclipse.pde.build_3.6.0.v20100603/scripts/productBuild/productBuild.xml -Dbuilder=/home/delphine/projects/biobank2/product/ > /home/delphine/projects/biobank2/product/output.txt

the folder given with -Dbuilder contains a build.properties and eventually others custom files like customTargets.xml. (see template folder to know what are the possible files).


