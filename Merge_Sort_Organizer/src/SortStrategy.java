import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public interface SortStrategy {
    List<Path> sort(List<Path> files);
    Map<String, List<Path>> categorize(List<Path> files);
}