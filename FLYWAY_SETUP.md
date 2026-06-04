# Flyway Database Migration Setup

## Overview
Flyway has been integrated into the Inventory Management System to manage database schema versioning and migrations. This ensures consistent database schema across all environments and provides version control for database changes.

## What Changed

### 1. **Dependencies Added** (build.gradle)
```groovy
implementation 'org.flywaydb:flyway-core'
implementation 'org.flywaydb:flyway-database-postgresql'
```

### 2. **Configuration Updated** (application.yml)
- Changed Hibernate `ddl-auto` from `update` to `validate`
- Added Flyway configuration for automatic schema management

### 3. **Migration Folder Structure**
```
src/main/resources/
└── db/
    └── migration/
        └── V1__Initial_schema.sql    # Initial database schema
```

## How Flyway Works

### Migration File Naming Convention
- **Pattern**: `V{version}__{description}.sql`
- **Examples**:
  - `V1__Initial_schema.sql` - First migration
  - `V2__Add_user_authentication.sql` - Second migration
  - `V3__Create_audit_tables.sql` - Third migration

### Key Features
1. **Automatic Version Tracking**: Flyway maintains a `flyway_schema_history` table
2. **Atomic Migrations**: Each migration runs in a transaction
3. **Out-of-Order Protection**: Prevents accidental out-of-order migrations
4. **Baseline Support**: Can baseline existing databases

## Creating New Migrations

### Step 1: Create a New Migration File
Create a new SQL file in `src/main/resources/db/migration/`:
```sql
-- V2__Add_users_table.sql
CREATE TABLE IF NOT EXISTS tbl_user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_user_email ON tbl_user(email);
```

### Step 2: Increment Version Number
Each new migration must have a higher version number than the previous one.

### Step 3: Run the Application
Flyway will automatically:
1. Detect the new migration
2. Execute it on application startup
3. Record it in `flyway_schema_history`

## Flyway Configuration (application.yml)

```yaml
spring:
  flyway:
    enabled: true                    # Enable/disable Flyway
    baseline-on-migrate: true        # Baseline existing databases
    locations: classpath:db/migration # Migration folder location
    out-of-order: false              # Prevent out-of-order migrations
```

## Environment-Specific Configuration

### Development (application.yml)
- Uses PostgreSQL database
- Flyway enabled with auto-migration

### Testing (application-test.yml)
- Uses H2 in-memory database
- Flyway enabled for consistent test schema
- Runs migrations before each test

## Running Migrations

### Automatically (On Application Startup)
Flyway runs migrations automatically when the application starts:
```bash
./gradlew bootRun
```

### Manually (Using Gradle)
```bash
./gradlew flywayMigrate
```

### Validate Database Schema
```bash
./gradlew flywayValidate
```

### View Migration History
Query the database:
```sql
SELECT * FROM flyway_schema_history;
```

## Migration Status

| Status | Description |
|--------|-------------|
| SUCCESS | Migration executed successfully |
| FAILED | Migration failed (manual intervention required) |
| PENDING | Migration waiting to be executed |
| IGNORED | Migration ignored (older than applied migrations) |

## Best Practices

### 1. **Idempotent Migrations**
```sql
-- Use IF NOT EXISTS to make migrations safe
CREATE TABLE IF NOT EXISTS tbl_example (
    id BIGSERIAL PRIMARY KEY
);

-- Use IF EXISTS for dropping
DROP TABLE IF EXISTS tbl_old_table;
```

### 2. **Backward Compatibility**
Always maintain backward compatibility in migrations.

### 3. **Version Control**
- Never modify existing migration files
- Always create new migration files for changes
- Keep migration files in version control

### 4. **Transaction Safety**
Each migration runs in a transaction and automatically rolls back on failure.

### 5. **Naming Convention**
- Use clear, descriptive names for migrations
- Separate multiple operations into different migrations when possible

## Troubleshooting

### Issue: "Detected failed migration"
**Solution**: Review the failed migration, fix it, and create a new migration file (don't modify the failed one).

### Issue: "Out of order migrations detected"
**Solution**: Ensure migration version numbers are sequential and in order.

### Issue: "Database schema validation error"
**Solution**: Run `./gradlew flywayValidate` to check schema consistency.

## Initial Migration (V1__Initial_schema.sql)

The initial migration includes:
- **Tables**: Store, Product, Stock, Purchase, PurchaseItem, Transfer, TransferItem, Adjustment, AdjustmentItem
- **Foreign Keys**: Proper relationships between tables
- **Indices**: Performance optimization for common queries
- **Constraints**: Unique constraints and validation rules

## Important Notes

1. **Never use Hibernate's ddl-auto: create/update in production**
   - Use `validate` mode with Flyway
   - This prevents accidental schema changes

2. **Database Configuration**
   - Ensure database user has DDL permissions
   - PostgreSQL: Tested and configured
   - H2: Used for testing

3. **Transactions**
   - Each migration is atomic
   - Failures trigger automatic rollback

4. **Version Control**
   - Always commit migration files to git
   - Migration history is reproducible

## Next Steps

1. Run the application to apply initial migration:
   ```bash
   ./gradlew clean build
   ./gradlew bootRun
   ```

2. Verify migration execution:
   ```sql
   SELECT * FROM flyway_schema_history;
   ```

3. Start creating new migrations as needed for schema changes

---

For more information, visit: https://flywaydb.org/documentation/

