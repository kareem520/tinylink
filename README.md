# TinyLink - URL Shortener 🚀

A scalable URL Shortener backend built with Spring Boot.  
The application allows users to create short URLs, redirect visitors, track analytics, apply rate limiting, and cache frequently accessed URLs using Redis.

---

## 📌 Features

### Authentication & Authorization
- JWT based authentication
- User registration and login
- Role based authorization

### URL Management
- Create short URLs
- Redirect short URLs to original URLs
- Expiration support
- Automatic cleanup of expired URLs

### Analytics
Track:
- Total clicks
- IP Address
- User Agent
- Referrer
- Country
- City
- Recent clicks history

### Performance
- Redis caching for URLs
- Redis based rate limiting
- Database optimization

### Storage
- AWS S3 integration for storing user assets

### API Documentation
- Swagger OpenAPI documentation

### Deployment
- Dockerized Spring Boot application
- Docker Redis container
- External MySQL database

---
