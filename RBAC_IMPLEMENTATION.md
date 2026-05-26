# RBAC (Role-Based Access Control) Implementation Guide

## Overview
This document describes the Role-Based Access Control (RBAC) implementation for the Inventory Management System. The system uses permissions grouped by functional areas and roles that combine multiple permissions.

## Components

### 1. Entities

#### Permission
- **Location**: `src/main/java/yoyo/inventory/entities/Permission.java`
- **Fields**:
  - `id` (Long, Primary Key)
  - `code` (String, Unique) - Permission identifier (e.g., "product:read")
  - `name` (String) - Display name
  - `description` (String)
  - `group` (PermissionGroup, Foreign Key) - Groups permissions by feature area
  
#### PermissionGroup
- **Location**: `src/main/java/yoyo/inventory/entities/PermissionGroup.java`
- **Fields**:
  - `id` (Long, Primary Key)
  - `code` (String, Unique) - Group identifier (e.g., "PRODUCT")
  - `name` (String)
  - `description` (String)

#### Role
- **Location**: `src/main/java/yoyo/inventory/entities/Role.java`
- **Fields**:
  - `id` (Long, Primary Key)
  - `code` (String, Unique) - Role identifier (e.g., "ROLE_ADMIN")
  - `name` (String)
  - `description` (String)
  - `permissions` (Set<Permission>, Many-to-Many) - Permissions assigned to this role
  - `createdAt` (LocalDateTime)
  - `updatedAt` (LocalDateTime)

#### User
- **Location**: `src/main/java/yoyo/inventory/entities/User.java`
- **Related Field**:
  - `roles` (Set<Role>, Many-to-Many) - Roles assigned to user

### 2. Roles

Five predefined roles are created during application startup:

#### ROLE_ADMIN (Administrator)
- **Permissions**: All permissions
- **Responsibility**: System administration, user management, role configuration

#### ROLE_MANAGER (Manager)
- **Permissions**: 
  - All master data management (create, read, update)
  - All transaction operations (create, read, update, approve)
  - Report viewing and export
- **Responsibility**: Business operations oversight, approval authority

#### ROLE_SUPERVISOR (Supervisor)
- **Permissions**:
  - Master data viewing only
  - Transaction operations (create, read, update)
  - Report viewing and export
- **Responsibility**: Operational oversight without approval authority

#### ROLE_STAFF (Staff)
- **Permissions**:
  - Master data viewing only
  - Transaction creation and viewing
  - Report viewing
- **Responsibility**: Daily operational tasks

#### ROLE_VIEWER (Viewer)
- **Permissions**: Read-only access to all entities
- **Responsibility**: View-only access for reporting and audit

### 3. Permission Groups & Permissions

```
USER
  ├── user:read (View Users)
  ├── user:create (Create Users)
  ├── user:update (Update Users)
  └── user:delete (Delete Users)

ROLE
  ├── role:read (View Roles)
  ├── role:create (Create Roles)
  ├── role:update (Update Roles)
  └── role:delete (Delete Roles)

PERMISSION
  ├── permission:read
  ├── permission:create
  ├── permission:update
  └── permission:delete

CATEGORY
  ├── category:read
  ├── category:create
  ├── category:update
  └── category:delete

SUB_CATEGORY
  ├── subcategory:read
  ├── subcategory:create
  ├── subcategory:update
  └── subcategory:delete

PRODUCT
  ├── product:read
  ├── product:create
  ├── product:update
  └── product:delete

UNIT
  ├── unit:read
  ├── unit:create
  ├── unit:update
  └── unit:delete

SUPPLIER
  ├── supplier:read
  ├── supplier:create
  ├── supplier:update
  └── supplier:delete

SELLER
  ├── seller:read
  ├── seller:create
  ├── seller:update
  └── seller:delete

STORE
  ├── store:read
  ├── store:create
  ├── store:update
  └── store:delete

CUSTOMER
  ├── customer:read
  ├── customer:create
  ├── customer:update
  └── customer:delete

PURCHASE
  ├── purchase:read
  ├── purchase:create
  ├── purchase:update
  ├── purchase:delete
  └── purchase:approve

SALE
  ├── sale:read
  ├── sale:create
  ├── sale:update
  ├── sale:delete
  └── sale:approve

TRANSFER
  ├── transfer:read
  ├── transfer:create
  ├── transfer:update
  ├── transfer:delete
  └── transfer:approve

ADJUSTMENT
  ├── adjustment:read
  ├── adjustment:create
  ├── adjustment:update
  ├── adjustment:delete
  └── adjustment:approve

REPORT
  ├── report:read
  └── report:export
```

### 4. Data Seeder

#### DataSeeder Configuration
- **Location**: `src/main/java/yoyo/inventory/config/DataSeeder.java`
- **Purpose**: Automatically seeds all permission groups, permissions, roles, and default admin user on application startup
- **Execution**: Runs as a Spring `CommandLineRunner` bean within a transaction

#### Seeded Data

**Permission Groups**: 16 groups covering all feature areas

**Permissions**: 
- 68 total permissions (4 CRUD operations per group, plus approval permissions for key transactions)

**Roles**:
1. ROLE_ADMIN - Full access
2. ROLE_MANAGER - Management permissions
3. ROLE_SUPERVISOR - Operational permissions
4. ROLE_STAFF - Limited operational permissions
5. ROLE_VIEWER - Read-only access

**Default Admin User**:
```
Username: admin
Email: admin@inventory.local
Password: Admin@123 (encoded)
Role: ROLE_ADMIN
Status: Active and Verified
```

### 5. Security Implementation

#### Controller Security Annotations
The following controllers are protected with `@PreAuthorize` annotations:

**RoleController** (`src/main/java/yoyo/inventory/controllers/RoleController.java`)
```java
@GetMapping
@PreAuthorize("hasAuthority('role:read')")
public ResponseEntity<ApiResponse<List<RoleResponse>>> getAll()

@PostMapping
@PreAuthorize("hasAuthority('role:create')")
public ResponseEntity<ApiResponse<RoleResponse>> create()

@PutMapping("/{id}")
@PreAuthorize("hasAuthority('role:update')")
public ResponseEntity<ApiResponse<RoleResponse>> update()

@DeleteMapping("/{id}")
@PreAuthorize("hasAuthority('role:delete')")
public ResponseEntity<ApiResponse<Void>> delete()
```

**PermissionController** (`src/main/java/yoyo/inventory/controllers/PermissionController.java`)
- Similar pattern with `permission:read`, `permission:create`, `permission:update`, `permission:delete`

**ProductController** (`src/main/java/yoyo/inventory/controllers/ProductController.java`)
- Protected with `product:read`, `product:create`, `product:update`, `product:delete`

#### How Security Works

1. **Authentication**: User logs in and receives a JWT token
2. **Authorization**: Token contains user's roles
3. **Permission Checking**: When endpoint is accessed, `@PreAuthorize` checks if user has required permission
4. **Permission Authority**: Permissions are checked as authorities/granted authorities from user's roles

### 6. API Endpoints

#### Role Management
```
GET    /api/v1/role           - List all roles (requires: role:read)
GET    /api/v1/role/{id}      - Get role by ID (requires: role:read)
POST   /api/v1/role           - Create new role (requires: role:create)
PUT    /api/v1/role/{id}      - Update role (requires: role:update)
DELETE /api/v1/role/{id}      - Delete role (requires: role:delete)
```

#### Permission Management
```
GET    /api/v1/permission           - List all permissions (requires: permission:read)
GET    /api/v1/permission/{id}      - Get permission by ID (requires: permission:read)
POST   /api/v1/permission           - Create new permission (requires: permission:create)
PUT    /api/v1/permission/{id}      - Update permission (requires: permission:update)
DELETE /api/v1/permission/{id}      - Delete permission (requires: permission:delete)
```

#### Product Management
```
GET    /api/v1/product        - List products (requires: product:read)
GET    /api/v1/product/{id}   - Get product by ID (requires: product:read)
POST   /api/v1/product        - Create product (requires: product:create)
PUT    /api/v1/product/{id}   - Update product (requires: product:update)
DELETE /api/v1/product/{id}   - Delete product (requires: product:delete)
```

### 7. Database Schema

#### Key Tables

**role_permissions** (Junction table for many-to-many relationship)
- `role_id` (Foreign Key to roles)
- `permission_id` (Foreign Key to permissions)
- Unique constraint on (role_id, permission_id)

**user_roles** (Junction table for many-to-many relationship)
- `user_id` (Foreign Key to users)
- `role_id` (Foreign Key to roles)

### 8. Usage Examples

#### Login and Get Token
```bash
POST /api/v1/auth/login
{
  "username": "admin",
  "password": "Admin@123"
}

Response:
{
  "accessToken": "eyJhbGc...",
  "refreshToken": "eyJhbGc...",
  "user": {
    "id": 1,
    "username": "admin",
    "email": "admin@inventory.local",
    "roles": ["ROLE_ADMIN"]
  }
}
```

#### Access Protected Endpoint
```bash
GET /api/v1/product
Authorization: Bearer <token>
```

#### Request Denied Example
If user has ROLE_VIEWER only:
```bash
POST /api/v1/product
Authorization: Bearer <token>
```
Response: **403 Forbidden** - User does not have `product:create` permission

## Configuration Requirements

### Security Config
Ensure `SecurityConfig.java` is configured to:
1. Enable method-level security with `@EnableMethodSecurity`
2. Configure JWT token processing to extract roles/authorities
3. Set up authentication manager for login

### Repositories Required
- `RoleRepository` - CRUD operations for Role
- `PermissionRepository` - CRUD operations for Permission
- `PermissionGroupRepository` - CRUD operations for PermissionGroup
- `UserRepository` - CRUD operations for User

## How to Extend

### Add New Permission
1. Add new permission group (if needed) in `DataSeeder.java`
2. Add new permission in appropriate group
3. Assign to relevant roles
4. Add `@PreAuthorize` annotation to controller methods

### Add New Role
1. Create new role in `DataSeeder.java`
2. Assign permissions to the role
3. Test authorization logic

### Protect New Controller
1. Add `@EnableMethodSecurity` to SecurityConfig (if not already done)
2. Add `@PreAuthorize("hasAuthority('permission:code')")` to methods
3. Ensure permission exists in seeded data

## Testing

### Test Unauthorized Access
```bash
curl -X POST http://localhost:8080/api/v1/product \
  -H "Authorization: Bearer <viewer_token>" \
  -H "Content-Type: application/json"
# Should return 403 Forbidden
```

### Test Authorized Access
```bash
curl -X POST http://localhost:8080/api/v1/product \
  -H "Authorization: Bearer <admin_token>" \
  -H "Content-Type: application/json" \
  -d '{"name":"Product","price":100}'
# Should succeed with 201 Created
```

## Security Best Practices

1. **Always use HTTPS** in production
2. **Rotate JWT secret** regularly
3. **Use strong passwords** for default admin account
4. **Regularly audit permissions** assigned to roles
5. **Implement audit logging** for sensitive operations
6. **Monitor failed authorization attempts**
7. **Use appropriate role** for each user (least privilege principle)
8. **Consider time-based access** for sensitive operations

## Troubleshooting

### Permission Denied Even Though User Has Role
- Check if permission code in `@PreAuthorize` matches database
- Verify role is assigned to user
- Check that permission is assigned to role

### DataSeeder Not Running
- Ensure `DataSeeder.java` is in classpath
- Check logs for errors
- Verify `RbacSeedConfig` bean is not conflicting

### 403 Errors on All Endpoints
- Verify SecurityConfig has method security enabled
- Check JWT token is valid and contains roles
- Verify user has required permission

## Related Files

- `src/main/java/yoyo/inventory/config/DataSeeder.java` - Data seeding configuration
- `src/main/java/yoyo/inventory/config/SecurityConfig.java` - Security configuration
- `src/main/java/yoyo/inventory/entities/Role.java` - Role entity
- `src/main/java/yoyo/inventory/entities/Permission.java` - Permission entity
- `src/main/java/yoyo/inventory/entities/PermissionGroup.java` - Permission group entity
- `src/main/java/yoyo/inventory/controllers/RoleController.java` - Role controller
- `src/main/java/yoyo/inventory/controllers/PermissionController.java` - Permission controller
