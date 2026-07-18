# TinyLink - URL Shortener 🚀

A scalable URL Shortener backend built with Spring Boot.  
The application allows users to create short URLs, redirect visitors, track analytics, apply rate limiting, and cache frequently accessed URLs using Redis.

---

## What It Does

TinyLink is a scalable URL shortening backend that enables users to generate, manage, and analyze shortened links.

Key capabilities:

- Generate unique short URLs from long URLs using an efficient short-code generation strategy.
- Support custom aliases and configurable URL expiration.
- Handle high-speed URL redirection with Redis-based caching.
- Collect click analytics such as click count, user agent, IP address, and request metadata.
- Apply distributed-friendly IP-based rate limiting on URL creation:
  - 2 requests per minute per client
  - 10 requests per hour per client
- Automatically invalidate expired links through scheduled background cleanup tasks.
- Provide REST APIs documented with Swagger/OpenAPI.
- 
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
# 🛠 Technologies

## Backend
- Java 21
- Spring Boot
- Spring Security
- JWT
- Spring Data JPA
- Hibernate
- Lombok

## Database
- MySQL

## Cache
- Redis

## Cloud
- AWS S3

## Documentation
- Swagger / OpenAPI

## Deployment
- Docker

---

# 🚀 Running The Project

## Requirements

Install:

- Java 21
- Maven
- Docker
- MySQL

---

