import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.kwadratowamasakra.lightspigot.connection.user.PermissionManager;

class PermissionManagerTest {

    @Test
    void permissionsShouldBeAddedRemovedAndDeduplicated() {
        final PermissionManager permissions = new PermissionManager();

        Assertions.assertFalse(permissions.hasPermission("world.edit"));
        permissions.addPermission("world.edit");
        permissions.addPermission("world.edit");
        Assertions.assertTrue(permissions.hasPermission("world.edit"));
        permissions.removePermission("world.edit");
        Assertions.assertFalse(permissions.hasPermission("world.edit"));
    }

    @Test
    void permissionsShouldRemainCaseSensitive() {
        final PermissionManager permissions = new PermissionManager();
        permissions.addPermission("World.Edit");

        Assertions.assertTrue(permissions.hasPermission("World.Edit"));
        Assertions.assertFalse(permissions.hasPermission("world.edit"));
    }
}
