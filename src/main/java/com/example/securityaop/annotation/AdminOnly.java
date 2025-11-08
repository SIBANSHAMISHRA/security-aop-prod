package com.example.securityaop.annotation;

import java.lang.annotation.*;

/**
 * 🔒 AdminOnly
 * ---------------------------------------------------
 * Custom AOP annotation to restrict access of a method
 * to ADMIN-level users only.
 *
 * ✅ Used with {@link com.example.securityaop.aspect.AuthorizationAspect}
 * to dynamically check the current user's role before method execution.
 *
 * ✅ When a non-admin user invokes an @AdminOnly method,
 * an AccessDeniedException or custom error is thrown automatically.
 *
 * Example usage:
 * <pre>
 *     @AdminOnly(description = "Delete a user from the system")
 *     @PostMapping("/deleteUser")
 *     public ResponseEntity<?> deleteUser(@RequestParam String email) {
 *         ...
 *     }
 * </pre>
 *
 * Author: Sibansha Mishra
 * Project: Security AOP Framework (Industry Version)
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AdminOnly {

    /**
     * Optional description for logging and documentation.
     * Example: "Delete user account", "View system logs", etc.
     */
    String description() default "";
}
