package yoyo.inventory.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;
import yoyo.inventory.entities.Permission;
import yoyo.inventory.entities.PermissionGroup;
import yoyo.inventory.entities.Role;
import yoyo.inventory.entities.User;
import yoyo.inventory.repository.PermissionGroupRepository;
import yoyo.inventory.repository.PermissionRepository;
import yoyo.inventory.repository.RoleRepository;
import yoyo.inventory.repository.UserRepository;

import java.util.List;
import java.util.Set;

/**
 * Data Seeder for RBAC (Role-Based Access Control)
 * Seeds permission groups, permissions, roles and admin user
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataSeeder {
    private final PermissionGroupRepository permissionGroupRepository;
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TransactionTemplate transactionTemplate;

    @Bean
    CommandLineRunner seedRbacData() {
        return args -> {
            transactionTemplate.executeWithoutResult(status -> {
                try {
                    log.info("Starting RBAC data seeding...");
                    
                    // Create Permission Groups
                    PermissionGroup userGroup = createPermissionGroup("USER", "User Management");
                    PermissionGroup roleGroup = createPermissionGroup("ROLE", "Role Management");
                    PermissionGroup permissionGroup = createPermissionGroup("PERMISSION", "Permission Management");
                    PermissionGroup categoryGroup = createPermissionGroup("CATEGORY", "Category Management");
                    PermissionGroup subCategoryGroup = createPermissionGroup("SUB_CATEGORY", "Sub Category Management");
                    PermissionGroup productGroup = createPermissionGroup("PRODUCT", "Product Management");
                    PermissionGroup unitGroup = createPermissionGroup("UNIT", "Unit Management");
                    PermissionGroup supplierGroup = createPermissionGroup("SUPPLIER", "Supplier Management");
                    PermissionGroup sellerGroup = createPermissionGroup("SELLER", "Seller Management");
                    PermissionGroup storeGroup = createPermissionGroup("STORE", "Store Management");
                    PermissionGroup customerGroup = createPermissionGroup("CUSTOMER", "Customer Management");
                    PermissionGroup purchaseGroup = createPermissionGroup("PURCHASE", "Purchase Management");
                    PermissionGroup saleGroup = createPermissionGroup("SALE", "Sale Management");
                    PermissionGroup transferGroup = createPermissionGroup("TRANSFER", "Transfer Management");
                    PermissionGroup adjustmentGroup = createPermissionGroup("ADJUSTMENT", "Stock Adjustment Management");
                    PermissionGroup reportGroup = createPermissionGroup("REPORT", "Report Management");
                    PermissionGroup stockGroup = createPermissionGroup("STOCK", "Stock Management");

                    // Create Permissions for each group
                    // User Permissions
                    Permission userRead = createPermission(userGroup, "user:read", "View Users");
                    Permission userCreate = createPermission(userGroup, "user:create", "Create Users");
                    Permission userUpdate = createPermission(userGroup, "user:update", "Update Users");
                    Permission userDelete = createPermission(userGroup, "user:delete", "Delete Users");
                    
                    // Role Permissions
                    Permission roleRead = createPermission(roleGroup, "role:read", "View Roles");
                    Permission roleCreate = createPermission(roleGroup, "role:create", "Create Roles");
                    Permission roleUpdate = createPermission(roleGroup, "role:update", "Update Roles");
                    Permission roleDelete = createPermission(roleGroup, "role:delete", "Delete Roles");
                    
                    // Permission Permissions
                    Permission permissionRead = createPermission(permissionGroup, "permission:read", "View Permissions");
                    Permission permissionCreate = createPermission(permissionGroup, "permission:create", "Create Permissions");
                    Permission permissionUpdate = createPermission(permissionGroup, "permission:update", "Update Permissions");
                    Permission permissionDelete = createPermission(permissionGroup, "permission:delete", "Delete Permissions");
                    
                    // Category Permissions
                    Permission categoryRead = createPermission(categoryGroup, "category:read", "View Categories");
                    Permission categoryCreate = createPermission(categoryGroup, "category:create", "Create Categories");
                    Permission categoryUpdate = createPermission(categoryGroup, "category:update", "Update Categories");
                    Permission categoryDelete = createPermission(categoryGroup, "category:delete", "Delete Categories");
                    
                    // SubCategory Permissions
                    Permission subCategoryRead = createPermission(subCategoryGroup, "subcategory:read", "View SubCategories");
                    Permission subCategoryCreate = createPermission(subCategoryGroup, "subcategory:create", "Create SubCategories");
                    Permission subCategoryUpdate = createPermission(subCategoryGroup, "subcategory:update", "Update SubCategories");
                    Permission subCategoryDelete = createPermission(subCategoryGroup, "subcategory:delete", "Delete SubCategories");
                    
                    // Product Permissions
                    Permission productRead = createPermission(productGroup, "product:read", "View Products");
                    Permission productCreate = createPermission(productGroup, "product:create", "Create Products");
                    Permission productUpdate = createPermission(productGroup, "product:update", "Update Products");
                    Permission productDelete = createPermission(productGroup, "product:delete", "Delete Products");
                    
                    // Unit Permissions
                    Permission unitRead = createPermission(unitGroup, "unit:read", "View Units");
                    Permission unitCreate = createPermission(unitGroup, "unit:create", "Create Units");
                    Permission unitUpdate = createPermission(unitGroup, "unit:update", "Update Units");
                    Permission unitDelete = createPermission(unitGroup, "unit:delete", "Delete Units");
                    
                    // Supplier Permissions
                    Permission supplierRead = createPermission(supplierGroup, "supplier:read", "View Suppliers");
                    Permission supplierCreate = createPermission(supplierGroup, "supplier:create", "Create Suppliers");
                    Permission supplierUpdate = createPermission(supplierGroup, "supplier:update", "Update Suppliers");
                    Permission supplierDelete = createPermission(supplierGroup, "supplier:delete", "Delete Suppliers");
                    
                    // Seller Permissions
                    Permission sellerRead = createPermission(sellerGroup, "seller:read", "View Sellers");
                    Permission sellerCreate = createPermission(sellerGroup, "seller:create", "Create Sellers");
                    Permission sellerUpdate = createPermission(sellerGroup, "seller:update", "Update Sellers");
                    Permission sellerDelete = createPermission(sellerGroup, "seller:delete", "Delete Sellers");
                    
                    // Store Permissions
                    Permission storeRead = createPermission(storeGroup, "store:read", "View Stores");
                    Permission storeCreate = createPermission(storeGroup, "store:create", "Create Stores");
                    Permission storeUpdate = createPermission(storeGroup, "store:update", "Update Stores");
                    Permission storeDelete = createPermission(storeGroup, "store:delete", "Delete Stores");
                    
                    // Customer Permissions
                    Permission customerRead = createPermission(customerGroup, "customer:read", "View Customers");
                    Permission customerCreate = createPermission(customerGroup, "customer:create", "Create Customers");
                    Permission customerUpdate = createPermission(customerGroup, "customer:update", "Update Customers");
                    Permission customerDelete = createPermission(customerGroup, "customer:delete", "Delete Customers");
                    
                    // Purchase Permissions
                    Permission purchaseRead = createPermission(purchaseGroup, "purchase:read", "View Purchases");
                    Permission purchaseCreate = createPermission(purchaseGroup, "purchase:create", "Create Purchases");
                    Permission purchaseUpdate = createPermission(purchaseGroup, "purchase:update", "Update Purchases");
                    Permission purchaseDelete = createPermission(purchaseGroup, "purchase:delete", "Delete Purchases");
                    Permission purchaseApprove = createPermission(purchaseGroup, "purchase:approve", "Approve Purchases");
                    
                    // Sale Permissions
                    Permission saleRead = createPermission(saleGroup, "sale:read", "View Sales");
                    Permission saleCreate = createPermission(saleGroup, "sale:create", "Create Sales");
                    Permission saleUpdate = createPermission(saleGroup, "sale:update", "Update Sales");
                    Permission saleDelete = createPermission(saleGroup, "sale:delete", "Delete Sales");
                    Permission saleApprove = createPermission(saleGroup, "sale:approve", "Approve Sales");
                    
                    // Transfer Permissions
                    Permission transferRead = createPermission(transferGroup, "transfer:read", "View Transfers");
                    Permission transferCreate = createPermission(transferGroup, "transfer:create", "Create Transfers");
                    Permission transferUpdate = createPermission(transferGroup, "transfer:update", "Update Transfers");
                    Permission transferDelete = createPermission(transferGroup, "transfer:delete", "Delete Transfers");
                    Permission transferApprove = createPermission(transferGroup, "transfer:approve", "Approve Transfers");
                    
                    // Adjustment Permissions
                    Permission adjustmentRead = createPermission(adjustmentGroup, "adjustment:read", "View Adjustments");
                    Permission adjustmentCreate = createPermission(adjustmentGroup, "adjustment:create", "Create Adjustments");
                    Permission adjustmentUpdate = createPermission(adjustmentGroup, "adjustment:update", "Update Adjustments");
                    Permission adjustmentDelete = createPermission(adjustmentGroup, "adjustment:delete", "Delete Adjustments");
                    Permission adjustmentApprove = createPermission(adjustmentGroup, "adjustment:approve", "Approve Adjustments");

                    Permission stockRead = createPermission(stockGroup, "stock:read", "View Stock");



                    // Report Permissions
                    Permission reportRead = createPermission(reportGroup, "report:read", "View Reports");
                    Permission reportExport = createPermission(reportGroup, "report:export", "Export Reports");

                    // Create Roles
                    Role superAdmin = createRole("ROLE_SUPER_ADMIN", "Super Administrator");
                    Role admin = createRole("ROLE_ADMIN", "Administrator");
                    Role manager = createRole("ROLE_MANAGER", "Manager");
                    Role supervisor = createRole("ROLE_SUPERVISOR", "Supervisor");
                    Role staff = createRole("ROLE_STAFF", "Staff");
                    Role sale = createRole("ROLE_SALE", "Sale");
                    Role viewer = createRole("ROLE_VIEWER", "Viewer");

                    // Assign permissions to Super Admin Role (All permissions)
                    superAdmin.getPermissions().addAll(Set.of(
                            // User & Role & Permission permissions
                            userRead, userCreate, userUpdate, userDelete,
                            roleRead, roleCreate, roleUpdate, roleDelete,
                            permissionRead, permissionCreate, permissionUpdate, permissionDelete,
                            // Master data permissions
                            categoryRead, categoryCreate, categoryUpdate, categoryDelete,
                            subCategoryRead, subCategoryCreate, subCategoryUpdate, subCategoryDelete,
                            productRead, productCreate, productUpdate, productDelete,
                            unitRead, unitCreate, unitUpdate, unitDelete,
                            supplierRead, supplierCreate, supplierUpdate, supplierDelete,
                            sellerRead, sellerCreate, sellerUpdate, sellerDelete,
                            storeRead, storeCreate, storeUpdate, storeDelete,
                            customerRead, customerCreate, customerUpdate, customerDelete,
                            // Transaction permissions
                            purchaseRead, purchaseCreate, purchaseUpdate, purchaseDelete, purchaseApprove,
                            saleRead, saleCreate, saleUpdate, saleDelete, saleApprove,
                            transferRead, transferCreate, transferUpdate, transferDelete, transferApprove,
                            adjustmentRead, adjustmentCreate, adjustmentUpdate, adjustmentDelete, adjustmentApprove,
                            // Report permissions
                            reportRead, reportExport
                            ,stockRead
                    ));

                    // Assign permissions to Admin Role (All permissions)
                    admin.getPermissions().addAll(Set.of(
                            // User & Role & Permission permissions
                            userRead, userCreate, userUpdate, userDelete,
                            roleRead, roleCreate, roleUpdate, roleDelete,
                            permissionRead, permissionCreate, permissionUpdate, permissionDelete,
                            // Master data permissions
                            categoryRead, categoryCreate, categoryUpdate, categoryDelete,
                            subCategoryRead, subCategoryCreate, subCategoryUpdate, subCategoryDelete,
                            productRead, productCreate, productUpdate, productDelete,
                            unitRead, unitCreate, unitUpdate, unitDelete,
                            supplierRead, supplierCreate, supplierUpdate, supplierDelete,
                            sellerRead, sellerCreate, sellerUpdate, sellerDelete,
                            storeRead, storeCreate, storeUpdate, storeDelete,
                            customerRead, customerCreate, customerUpdate, customerDelete,
                            // Transaction permissions
                            purchaseRead, purchaseCreate, purchaseUpdate, purchaseDelete, purchaseApprove,
                            saleRead, saleCreate, saleUpdate, saleDelete, saleApprove,
                            transferRead, transferCreate, transferUpdate, transferDelete, transferApprove,
                            adjustmentRead, adjustmentCreate, adjustmentUpdate, adjustmentDelete, adjustmentApprove,
                            // Report permissions
                            reportRead, reportExport , stockRead
                    ));

                    // Assign permissions to Manager Role
                    manager.getPermissions().addAll(Set.of(
                            // User permissions (read only)
                            userRead,
                            // Master data permissions
                            categoryRead, categoryCreate, categoryUpdate,
                            subCategoryRead, subCategoryCreate, subCategoryUpdate,
                            productRead, productCreate, productUpdate,
                            unitRead, unitCreate, unitUpdate,
                            supplierRead, supplierCreate, supplierUpdate,
                            sellerRead, sellerCreate, sellerUpdate,
                            storeRead, storeCreate, storeUpdate,
                            customerRead, customerCreate, customerUpdate,
                            // Transaction permissions
                            purchaseRead, purchaseCreate, purchaseUpdate, purchaseApprove,
                            saleRead, saleCreate, saleUpdate, saleApprove,
                            transferRead, transferCreate, transferUpdate, transferApprove,
                            adjustmentRead, adjustmentCreate, adjustmentUpdate, adjustmentApprove,
                            // Report permissions
                            reportRead, reportExport , stockRead
                    ));

                    // Assign permissions to Supervisor Role
                    supervisor.getPermissions().addAll(Set.of(
                            // Master data permissions (read only)
                            categoryRead, subCategoryRead, productRead, unitRead,
                            supplierRead, sellerRead, storeRead, customerRead,
                            // Transaction permissions
                            purchaseRead, purchaseCreate, purchaseUpdate,
                            saleRead, saleCreate, saleUpdate,
                            transferRead, transferCreate, transferUpdate,
                            adjustmentRead, adjustmentCreate, adjustmentUpdate,
                            // Report permissions
                            reportRead, reportExport
                    ));

                    // Assign permissions to Staff Role
                    staff.getPermissions().addAll(Set.of(
                            // Master data permissions (read only)
                            categoryRead, subCategoryRead, productRead, unitRead,
                            supplierRead, sellerRead, storeRead, customerRead,
                            // Transaction permissions (create & read)
                            purchaseRead, purchaseCreate,
                            saleRead, saleCreate,
                            transferRead, transferCreate,
                            adjustmentRead, adjustmentCreate,
                            // Report permissions (read only)
                            reportRead
                    ));

                    // Assign permissions to Sale Role
                    sale.getPermissions().addAll(Set.of(
                            // Master data permissions (read only)
                            categoryRead, productRead, storeRead, customerRead, customerCreate,
                            // Transaction permissions (create & read)
                            saleRead, saleCreate,
                            // Report permissions (read only)
                            reportRead
                    ));

                    // Assign permissions to Viewer Role (Read-only)
                    viewer.getPermissions().addAll(Set.of(
                            categoryRead, subCategoryRead, productRead, unitRead,
                            supplierRead, sellerRead, storeRead, customerRead,
                            purchaseRead, saleRead, transferRead, adjustmentRead,
                            reportRead
                    ));

                    // Save all roles
                    roleRepository.saveAll(List.of(superAdmin, admin, manager, supervisor, staff, sale, viewer));
                    log.info("Roles created successfully");

                    // Create admin user
                    createAdminUser(superAdmin);
                    
                    log.info("RBAC data seeding completed successfully");
                } catch (Exception e) {
                    log.error("Error during RBAC data seeding", e);
                    throw e;
                }
            });
        };
    }

    /**
     * Create or get existing permission group
     */
    private PermissionGroup createPermissionGroup(String code, String name) {
        return permissionGroupRepository.findByCode(code).orElseGet(() -> {
            log.debug("Creating permission group: {} ({})", name, code);
            PermissionGroup group = new PermissionGroup();
            group.setCode(code);
            group.setName(name);
            group.setDescription(name);
            return permissionGroupRepository.save(group);
        });
    }

    /**
     * Create or get existing permission
     */
    private Permission createPermission(PermissionGroup group, String code, String name) {
        return permissionRepository.findByCode(code).orElseGet(() -> {
            log.debug("Creating permission: {} ({})", name, code);
            Permission permission = new Permission();
            permission.setGroup(group);
            permission.setCode(code);
            permission.setName(name);
            permission.setDescription(name);
            return permissionRepository.save(permission);
        });
    }

    /**
     * Create or get existing role
     */
    private Role createRole(String code, String name) {
        return roleRepository.findByCode(code).orElseGet(() -> {
            log.debug("Creating role: {} ({})", name, code);
            Role role = new Role();
            role.setCode(code);
            role.setName(name);
            role.setDescription("Role for " + name);
            return roleRepository.save(role);
        });
    }

    /**
     * Create or update admin user
     */
    private void createAdminUser(Role adminRole) {
        User adminUser = userRepository.findByUsername("admin").orElseGet(User::new);
        if (adminUser.getId() == null) {
            log.info("Creating admin user");
            adminUser.setUsername("admin");
            adminUser.setEmail("admin@inventory.local");
            adminUser.setFirstName("System");
            adminUser.setLastName("Admin");
            adminUser.setPasswordHash(passwordEncoder.encode("Admin@123"));
            adminUser.setIsActive(true);
            adminUser.setIsVerified(true);
        }
        adminUser.getRoles().clear();
        adminUser.getRoles().add(adminRole);
        userRepository.save(adminUser);
        log.info("Admin user created/updated successfully");
    }
}
