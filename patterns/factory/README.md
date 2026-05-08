# Factory Pattern Blueprint

## The WHY

Object creation is often conditional. You need an `Email` here, an `Sms` there,
a `Push` somewhere else — and the decision depends on runtime data.

Without a factory, this conditional logic scatters across every call site:

```java
// ANTI-PATTERN: creation logic duplicated at every call site
if (type == EMAIL) {
    notification = new Email(recipient, subject, body, defaultReplyTo, Instant.now());
} else if (type == SMS) {
    String truncated = body.length() > 160 ? body.substring(0, 157) + "..." : body;
    notification = new Sms(recipient, subject, truncated, phone, Instant.now());
} // ... repeated everywhere
```

Add a new type, add a bug in the truncation logic, change the default reply-to —
and you're hunting through every class that ever created a notification.

The factory centralises all of this. Call sites pass a descriptor, receive
a fully built object, and never touch construction logic again.

## Modern Java Features

This module uses two Java 21+ features that make the factory exceptionally clean:

### Sealed Interfaces
```java
public sealed interface Notification permits Notification.Email, Notification.Sms, Notification.Push {}
```
The compiler knows every possible subtype. No anonymous subclasses, no runtime surprises.

### Exhaustive Pattern Matching
```java
return switch (request.type()) {
    case EMAIL -> new Notification.Email(...);
    case SMS   -> new Notification.Sms(...);
    case PUSH  -> new Notification.Push(...);
};
// No default needed — compiler enforces exhaustiveness
```
Add `CARRIER_PIGEON` to the enum and this switch fails to compile until you handle it.
The compiler becomes your checklist.

## Business Logic in the Factory

The SMS body truncation is a business rule: SMS messages have a 160-character limit.
It belongs in the factory — not in the caller, not in the model, not in the controller.
The factory is responsible for producing a valid object; validation is part of creation.

## Running the Example

```bash
./mvnw spring-boot:run
# POST /notifications with {"type":"SMS","recipient":"...","subject":"...","body":"..."}
```
