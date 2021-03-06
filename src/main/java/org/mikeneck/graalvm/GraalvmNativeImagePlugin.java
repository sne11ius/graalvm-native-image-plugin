/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package org.mikeneck.graalvm;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.ProjectLayout;
import org.jetbrains.annotations.NotNull;

public class GraalvmNativeImagePlugin implements Plugin<Project> {

    @SuppressWarnings("UnstableApiUsage")
    public void apply(@NotNull Project project) {
        NativeImageTaskFactory taskFactory = new NativeImageTaskFactory(project);

        InstallNativeImageTask installNativeImageTask = taskFactory.installNativeImageTask(task -> {
            task.setGroup("graalvm");
            task.setDescription("Installs native-image command by graalVm Updater command");
        });

        GenerateNativeImageConfigTask nativeImageConfigFiles = taskFactory.nativeImageConfigFilesTask(task -> {
            task.setGroup("graalvm");
            task.setDescription("Generates native image config json files via test run.");
            task.dependsOn(installNativeImageTask);
        });

        MergeNativeImageConfigTask mergeNativeImageConfigTask = taskFactory.mergeNativeImageConfigTask(task -> {
            task.setGroup("graalvm");
            task.setDescription("Merge generated native-image-config json files into one file.");
            task.fromDirectories(project.provider(() -> {
                List<File> directories = nativeImageConfigFiles.getJavaExecutions()
                        .stream()
                        .map(JavaExecutionImpl::getOutputDirectory)
                        .collect(Collectors.toList());
                ConfigurableFileCollection collection = project.getObjects().fileCollection();
                collection.setFrom(directories);
                return collection;
            }));
            ProjectLayout layout = project.getLayout();
            task.destinationDir(layout.getBuildDirectory().dir("native-image-config"));
            nativeImageConfigFiles.finalizedBy(task);
        });

        nativeImageConfigFiles.shareEnabledStateWith(mergeNativeImageConfigTask);
        nativeImageConfigFiles.setEnabled(false);

        taskFactory.nativeImageTask(task -> {
            task.setGroup("graalvm");
            task.setDescription("Creates native executable");
            task.dependsOn(installNativeImageTask, nativeImageConfigFiles);
            task.withConfigFiles(files -> files.fromMergeTask(mergeNativeImageConfigTask));
        });
    }
}
