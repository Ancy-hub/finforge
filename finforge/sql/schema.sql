-- ============================================================
-- Smart FinForge - SQL Server Database Schema
-- ============================================================

USE master;
GO

-- Create database if it does not exist
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'SmartFinForge')
BEGIN
    CREATE DATABASE SmartFinForge
    COLLATE SQL_Latin1_General_CP1_CI_AS;
    PRINT 'Database SmartFinForge created.';
END
GO

USE SmartFinForge;
GO

-- ============================================================
-- Drop existing tables (in dependency order)
-- ============================================================

IF OBJECT_ID('dbo.expenses',   'U') IS NOT NULL DROP TABLE dbo.expenses;
IF OBJECT_ID('dbo.incomes',    'U') IS NOT NULL DROP TABLE dbo.incomes;
IF OBJECT_ID('dbo.categories', 'U') IS NOT NULL DROP TABLE dbo.categories;
IF OBJECT_ID('dbo.users',      'U') IS NOT NULL DROP TABLE dbo.users;
GO

-- ============================================================
-- TABLE: users
-- ============================================================

CREATE TABLE dbo.users (
    user_id       INT            NOT NULL IDENTITY(1,1),
    username      VARCHAR(50)    NOT NULL,
    email         VARCHAR(100)   NOT NULL,
    password_hash VARCHAR(64)    NOT NULL,
    first_name    VARCHAR(50)    NOT NULL,
    last_name     VARCHAR(50)    NOT NULL,
    phone         VARCHAR(15)    NULL,
    created_at    DATETIME       NOT NULL CONSTRAINT DF_users_created_at  DEFAULT GETDATE(),
    updated_at    DATETIME       NOT NULL CONSTRAINT DF_users_updated_at  DEFAULT GETDATE(),
    is_active     BIT            NOT NULL CONSTRAINT DF_users_is_active   DEFAULT 1,

    CONSTRAINT PK_users              PRIMARY KEY (user_id),
    CONSTRAINT UQ_users_username     UNIQUE      (username),
    CONSTRAINT UQ_users_email        UNIQUE      (email),
    CONSTRAINT CK_users_email_format CHECK       (email LIKE '%_@_%.__%'),
    CONSTRAINT CK_users_username_len CHECK       (LEN(username) >= 3)
);
GO

-- ============================================================
-- TABLE: categories
-- ============================================================

CREATE TABLE dbo.categories (
    category_id INT          NOT NULL IDENTITY(1,1),
    name        VARCHAR(50)  NOT NULL,
    description VARCHAR(200) NULL,
    user_id     INT          NOT NULL,
    created_at  DATETIME     NOT NULL CONSTRAINT DF_categories_created_at DEFAULT GETDATE(),

    CONSTRAINT PK_categories             PRIMARY KEY (category_id),
    CONSTRAINT FK_categories_users       FOREIGN KEY (user_id)
        REFERENCES dbo.users(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT UQ_categories_name_user   UNIQUE (name, user_id)
);
GO

-- ============================================================
-- TABLE: expenses
-- ============================================================

CREATE TABLE dbo.expenses (
    expense_id   INT            NOT NULL IDENTITY(1,1),
    title        VARCHAR(100)   NOT NULL,
    description  VARCHAR(500)   NULL,
    amount       DECIMAL(10,2)  NOT NULL,
    category_id  INT            NOT NULL,
    user_id      INT            NOT NULL,
    expense_date DATE           NOT NULL,
    created_at   DATETIME       NOT NULL CONSTRAINT DF_expenses_created_at DEFAULT GETDATE(),
    updated_at   DATETIME       NOT NULL CONSTRAINT DF_expenses_updated_at DEFAULT GETDATE(),

    CONSTRAINT PK_expenses            PRIMARY KEY (expense_id),
    CONSTRAINT FK_expenses_categories FOREIGN KEY (category_id)
        REFERENCES dbo.categories(category_id),
    CONSTRAINT FK_expenses_users      FOREIGN KEY (user_id)
        REFERENCES dbo.users(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT CK_expenses_amount     CHECK (amount > 0),
    CONSTRAINT CK_expenses_title_len  CHECK (LEN(LTRIM(title)) > 0)
);
GO

-- ============================================================
-- TABLE: incomes
-- ============================================================

CREATE TABLE dbo.incomes (
    income_id   INT           NOT NULL IDENTITY(1,1),
    source      VARCHAR(100)  NOT NULL,
    amount      DECIMAL(10,2) NOT NULL,
    income_date DATE          NOT NULL,
    user_id     INT           NOT NULL,
    created_at  DATETIME      NOT NULL CONSTRAINT DF_incomes_created_at DEFAULT GETDATE(),
    updated_at  DATETIME      NOT NULL CONSTRAINT DF_incomes_updated_at DEFAULT GETDATE(),

    CONSTRAINT PK_incomes           PRIMARY KEY (income_id),
    CONSTRAINT FK_incomes_users     FOREIGN KEY (user_id)
        REFERENCES dbo.users(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT CK_incomes_amount    CHECK (amount > 0),
    CONSTRAINT CK_incomes_src_len   CHECK (LEN(LTRIM(source)) > 0)
);
GO

-- ============================================================
-- Performance Indexes
-- ============================================================

CREATE NONCLUSTERED INDEX IX_expenses_user_id
    ON dbo.expenses(user_id) INCLUDE (expense_date, amount, category_id);

CREATE NONCLUSTERED INDEX IX_expenses_category_id
    ON dbo.expenses(category_id);

CREATE NONCLUSTERED INDEX IX_expenses_date
    ON dbo.expenses(expense_date);

CREATE NONCLUSTERED INDEX IX_incomes_user_id
    ON dbo.incomes(user_id) INCLUDE (income_date, amount);

CREATE NONCLUSTERED INDEX IX_incomes_date
    ON dbo.incomes(income_date);

CREATE NONCLUSTERED INDEX IX_categories_user_id
    ON dbo.categories(user_id);
GO

-- ============================================================
-- Stored Procedure: Seed Default Categories for new user
-- ============================================================

CREATE OR ALTER PROCEDURE dbo.sp_SeedDefaultCategories
    @user_id INT
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO dbo.categories (name, description, user_id) VALUES
        ('Food',          'Food and dining expenses',          @user_id),
        ('Travel',        'Transportation and travel expenses', @user_id),
        ('Rent',          'Rent and housing expenses',          @user_id),
        ('Medical',       'Healthcare and medical expenses',    @user_id),
        ('Shopping',      'Shopping and retail expenses',       @user_id),
        ('Utilities',     'Utility bills and services',         @user_id),
        ('Entertainment', 'Entertainment and recreation',       @user_id);
END;
GO

-- ============================================================
-- Sample Data (optional - comment out for production)
-- ============================================================

-- Insert a demo user (password: Admin@123 -> SHA-256 hash)
-- SHA-256 of 'Admin@123' = 'c7ad44cbad762a5da0a452f9e854fdc1e0e7a52a38015f23f3eab1d80b931dd472634dfac71cd34ebc35d16ab7fb8a90c81f975113d6c7538dc69dd8de9077ec'
-- Note: Use PasswordUtil.hashPassword() to generate real hashes for your passwords

PRINT 'Schema created successfully for SmartFinForge.';
GO
