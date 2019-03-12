package com.ullink

import org.gradle.api.internal.AbstractTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.javadoc.Javadoc

class IkvmDoc extends Javadoc {
    def assemblyFile
    IkvmDoc() {
        conventionMapping.map "classpath", { project.configurations.compile }
        conventionMapping.map "source", { project.sourceSets.main.allJava }
        conventionMapping.map "assemblyFile", {
            dependsOn(project.tasks.ikvm)
            project.tasks.ikvm.getDestFile()
        }
        options.doclet = IKVMDocLet.class.getName()
        def pluginClasspath = project.getBuildscript().getConfigurations().getByName('classpath').files.asType(List)
        if(pluginClasspath.isEmpty()) {
            options.setDocletpath(project.getRootProject().getBuildscript().getConfigurations().getByName('classpath').files.asType(List))
        } else {
            options.setDocletpath(project.getBuildscript().getConfigurations().getByName('classpath').files.asType(List))
        }
    }
    
    protected void setOutput(AbstractTask task) {
        task.getOutputs().file {
            getDestinationFile()
        }
    }
    
    File getDestinationFile() {
        File assembly = getAbsoluteAssemblyFile()
        String assemblyName = assembly.name.substring(0, assembly.name.lastIndexOf('.'))
        new File(assembly.getParent(), assemblyName + ".xml");
    }
    
    // remove @Output annotation
    @Override
    public File getDestinationDir() {
    }
    
    @Override
    @TaskAction
    protected void generate()
    {
        options.addStringOption("assembly", getAbsoluteAssemblyFile().toString())
        super.generate();
    }
    
    @InputFile
    File getAbsoluteAssemblyFile() {
        project.file(getAssemblyFile())
    }
}
