package pl.kwadratowamasakra.lightspigot.connection.user;

import java.util.HashSet;
import java.util.Set;

/**
 * The PermissionManager class manages the permissions of a user.
 * It includes methods to add, remove and check permissions.
 */
public class PermissionManager {

    private final Set<String> permissions = new HashSet<>();

    /**
     * Adds a permission to the list of permissions.
     * If the permission already exists in the list, it is not added again.
     *
     * @param permission The permission to add.
     */
    public final void addPermission(final String permission) {
        permissions.add(permission);
    }

    /**
     * Removes a permission from the list of permissions.
     *
     * @param permission The permission to remove.
     */
    public final void removePermission(final String permission) {
        permissions.remove(permission);
    }

    /**
     * Checks if a permission exists in the list of permissions.
     *
     * @param permission The permission to check.
     * @return True if the permission exists in the list, false otherwise.
     */
    public final boolean hasPermission(final String permission) {
        return permissions.contains(permission);
    }
}
