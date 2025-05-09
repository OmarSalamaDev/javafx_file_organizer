import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class SizeSortStrategy implements SortStrategy {
    @Override
    public List<Path> sort(List<Path> files) {
        if (files.size() <= 1) return new ArrayList<>(files);

        int mid = files.size() / 2;
        List<Path> left = sort(files.subList(0, mid));
        List<Path> right = sort(files.subList(mid, files.size()));

        return merge(left, right);
    }

    private List<Path> merge(List<Path> left, List<Path> right) {
        List<Path> merged = new ArrayList<>();
        int i = 0, j = 0;

        while (i < left.size() && j < right.size()) {
            try {
                if (Files.size(left.get(i)) <= Files.size(right.get(j))) {
                    merged.add(left.get(i++));
                } else {
                    merged.add(right.get(j++));
                }
            } catch (IOException e) {
                merged.add(left.get(i++));
            }
        }

        merged.addAll(left.subList(i, left.size()));
        merged.addAll(right.subList(j, right.size()));

        return merged;
    }

    @Override
    public Map<String, List<Path>> categorize(List<Path> files) {
        Map<String, List<Path>> groups = new HashMap<>();

        for (Path file : files) {
            try {
                String category = getSizeCategory(Files.size(file));
                groups.computeIfAbsent(category, k -> new ArrayList<>()).add(file);
            } catch (IOException e) {
                groups.computeIfAbsent("unknown", k -> new ArrayList<>()).add(file);
            }
        }
        return groups;
    }

    private String getSizeCategory(long size) {
        if (size < 1024) return "tiny";
        if (size < 1024 * 1024) return "small";
        if (size < 1024 * 1024 * 10) return "medium";
        return "large";
    }
}