import java.nio.file.Path;
import java.util.*;

public class NameSortStrategy implements SortStrategy {
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
            if (compare(left.get(i), right.get(j)) <= 0) {
                merged.add(left.get(i++));
            } else {
                merged.add(right.get(j++));
            }
        }

        merged.addAll(left.subList(i, left.size()));
        merged.addAll(right.subList(j, right.size()));

        return merged;
    }

    private int compare(Path a, Path b) {
        return a.getFileName().toString().compareTo(b.getFileName().toString());
    }

    @Override
    public Map<String, List<Path>> categorize(List<Path> files) {
        Map<String, List<Path>> groups = new HashMap<>();
        for (Path file : files) {
            String firstChar = file.getFileName().toString().substring(0, 1).toLowerCase();
            groups.computeIfAbsent(firstChar, k -> new ArrayList<>()).add(file);
        }
        return groups;
    }
}