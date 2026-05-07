# Thymeleaf Layout Integration Guide for Spring Boot

## Overview
This project uses **Thymeleaf** templating engine with **Thymeleaf Layout Dialect** for layout inheritance. This is the standard and optimal approach for Spring Boot Java applications.

## Dependencies (pom.xml)

Add these dependencies to your Spring Boot project:

```xml
<dependencies>
    <!-- Thymeleaf -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-thymeleaf</artifactId>
    </dependency>
    
    <!-- Thymeleaf Layout Dialect -->
    <dependency>
        <groupId>nz.net.ultraq.thymeleaf</groupId>
        <artifactId>thymeleaf-layout-dialect</artifactId>
        <version>3.2.1</version>
    </dependency>
</dependencies>
```

## Project Structure

```
src/main/resources/
├── templates/
│   ├── layouts/
│   │   ├── admin-layout.html    # Admin sidebar layout
│   │   └── user-layout.html     # User header/footer layout
│   ├── admin/
│   │   ├── dashboard.html
│   │   ├── admin.html
│   │   ├── studio.html
│   │   ├── social.html
│   │   ├── writer.html
│   │   ├── projects.html
│   │   ├── assets.html
│   │   └── settings.html
│   └── user/
│       ├── index.html
│       ├── login.html
│       ├── register.html
│       ├── pricing.html
│       ├── intro-automation.html
│       ├── intro-studio.html
│       └── intro-writer.html
└── static/
    ├── css/
    │   └── style.css
    ├── js/
    │   └── script.js
    └── images/
        └── mockup.png
```

## How to Use Layouts

### Admin Pages Example

```html
<!DOCTYPE html>
<html lang="vi" 
      xmlns:th="http://www.thymeleaf.org" 
      xmlns:layout="http://www.ultraq.nz.nz/thymeleaf/layout"
      layout:decorate="~{layouts/admin-layout}">
<head>
    <title>Bảng điều khiển | SuoiDesign</title>
</head>
<body>
    <main layout:fragment="content">
        <!-- Your page content goes here -->
        <h2>Dashboard Content</h2>
    </main>
</body>
</html>
```

### User Pages Example

```html
<!DOCTYPE html>
<html lang="vi" 
      xmlns:th="http://www.thymeleaf.org" 
      xmlns:layout="http://www.ultraq.nz.nz/thymeleaf/layout"
      layout:decorate="~{layouts/user-layout}">
<head>
    <title>Trang chủ | SuoiDesign</title>
</head>
<body>
    <main layout:fragment="content">
        <!-- Your page content goes here -->
        <h2>Home Page Content</h2>
    </main>
</body>
</html>
```

## Key Thymeleaf Attributes

- `layout:decorate="~{layout-path}"` - Specifies which layout template to use
- `layout:fragment="content"` - Defines where content will be injected in the layout
- `th:text="${title}"` - Dynamic text from server-side
- `th:href="@{/path}"` - URL rewriting for links
- `th:src="@{/path}"` - URL rewriting for static resources

## Spring Boot Controller Example

```java
@Controller
public class AdminController {
    
    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("title", "Bảng điều khiển | SuoiDesign");
        return "admin/dashboard";
    }
    
    @GetMapping("/admin/studio")
    public String studio(Model model) {
        model.addAttribute("title", "AI Studio | SuoiDesign");
        return "admin/studio";
    }
}

@Controller
public class UserController {
    
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("title", "SuoiDesign | Future of AI");
        return "user/index";
    }
    
    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("title", "Đăng nhập | SuoiDesign");
        return "user/login";
    }
}
```

## Benefits of This Approach

1. **DRY Principle** - No duplicate sidebar/header/footer code
2. **Easy Maintenance** - Update layout once, reflects across all pages
3. **Spring Boot Native** - Built-in support, no additional configuration needed
4. **Type-Safe** - Compile-time checking with Spring Boot
5. **SEO Friendly** - Server-side rendering for better SEO

## Migration Steps

1. Move `layouts/` folder to `src/main/resources/templates/layouts/`
2. Move `admin/` folder to `src/main/resources/templates/admin/`
3. Move `user/` folder to `src/main/resources/templates/user/`
4. Move `assets/` folder to `src/main/resources/static/`
5. Update each HTML file to use `layout:decorate` attribute
6. Add Thymeleaf dependencies to pom.xml
7. Create Spring Boot controllers

## Static Resources

Static files (CSS, JS, images) should be in `src/main/resources/static/`:
- `src/main/resources/static/css/style.css`
- `src/main/resources/static/js/script.js`
- `src/main/resources/static/images/mockup.png`

Reference them in templates as:
```html
<link rel="stylesheet" th:href="@{/css/style.css}">
<script th:src="@{/js/script.js}"></script>
```

## Example Files

See:
- `admin/dashboard-example.html` - Complete admin page example
- `user/index-example.html` - Complete user page example
