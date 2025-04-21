public class OrganizerSettings {
    private final boolean createSubfolders;
    private final boolean recursive;

    private OrganizerSettings(boolean createSubfolders, boolean recursive) {
        this.createSubfolders = createSubfolders;
        this.recursive = recursive;
    }

    public static OrganizerSettings create(boolean isSub, boolean isRec) {
        return new OrganizerSettings(isSub, isRec);
    }

    public boolean shouldCreateSubfolders() {
        return createSubfolders;
    }

    public boolean isRecursive() {
        return recursive;
    }
}