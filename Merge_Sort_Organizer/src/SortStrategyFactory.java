public class SortStrategyFactory {
    public static SortStrategy create(String crt) {

        if (!crt.isEmpty()) {
            String criteria = crt.toUpperCase();
            switch (criteria) {
                case "SIZE": return new SizeSortStrategy();
                case "NAME": return new NameSortStrategy();
                default: return new ExtensionSortStrategy();
            }
        }
        return new ExtensionSortStrategy();
    }
}