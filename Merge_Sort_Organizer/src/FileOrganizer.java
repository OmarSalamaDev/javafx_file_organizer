import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public class FileOrganizer {
    private final Path rootDir;
    private SortStrategy sortStrategy;
    private int fileCount;
    private int dirCount;

    public FileOrganizer(String rootDirPath) {
        this.rootDir = Paths.get(rootDirPath).toAbsolutePath();
    }

    public void setSortStrategy(SortStrategy strategy) {
        this.sortStrategy = strategy;
    }

    public void organize(OrganizerSettings settings) {
        try {
            if (settings.isRecursive()) {
                processDirectoryRecursive(rootDir, settings);
            } else {
                processDirectory(rootDir, settings);
            }
            System.out.printf("Organization complete. Processed %d files and %d directories.%n",
                    fileCount, dirCount);
        } catch (IOException e) {
            System.err.println("Error organizing files: " + e.getMessage());
        }
    }

    private void processDirectoryRecursive(Path dir, OrganizerSettings settings) throws IOException {
        try (Stream<Path> paths = Files.list(dir)) {
            List<Path> subDirs = paths.filter(Files::isDirectory).toList();

            for (Path subDir : subDirs) {
                try {
                    processDirectoryRecursive(subDir, settings);
                } catch (IOException e) {
                    System.err.println("Error processing directory: " + subDir);
                }
            }
        }

        processDirectory(dir, settings);
    }


    private void processDirectory(Path dir, OrganizerSettings settings) throws IOException {
        List<Path> files = getFilesInDirectory(dir);
        fileCount += files.size();

        if (files.isEmpty()) return;

        List<Path> sortedFiles = sortStrategy.sort(files);

        if (settings.shouldCreateSubfolders()) {
            organizeIntoSubfolders(dir, sortedFiles);
        }
    }

    public List<Path> getFilesInDirectory(Path dir) throws IOException {
        try (Stream<Path> paths = Files.list(dir)) {
            return paths.filter(Files::isRegularFile)
                    .collect(Collectors.toList());
        }
    }

    private void organizeIntoSubfolders(Path dir, List<Path> files) throws IOException {
        Map<String, List<Path>> groups = sortStrategy.categorize(files);

        for (Map.Entry<String, List<Path>> entry : groups.entrySet()) {
            Path categoryDir = dir.resolve(entry.getKey());
            if (!Files.exists(categoryDir)) {
                Files.createDirectory(categoryDir);
                dirCount++;
            }

            for (Path file : entry.getValue()) {
                Path target = categoryDir.resolve(file.getFileName());
                Files.move(file, target, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }
}