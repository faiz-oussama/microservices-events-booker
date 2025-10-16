# Ticket Service

This is the ticket service for the event ticketing platform. It manages tickets, orders, and events.

## API Endpoints

### Tickets

- `GET /api/tickets` - Get all tickets
- `GET /api/tickets/{id}` - Get a specific ticket
- `POST /api/tickets` - Create a new ticket
- `PUT /api/tickets/{id}` - Update a ticket
- `DELETE /api/tickets/{id}` - Delete a ticket
- `GET /api/tickets/available` - Get all available tickets
- `GET /api/tickets/event/{eventId}` - Get tickets for a specific event
- `GET /api/tickets/user/{userId}` - Get tickets for a specific user

### Events

- `GET /api/events` - Get all events
- `GET /api/events/{id}` - Get a specific event
- `POST /api/events` - Create a new event
- `PUT /api/events/{id}` - Update an event
- `DELETE /api/events/{id}` - Delete an event

### Orders

- `GET /api/orders` - Get all orders
- `GET /api/orders/{id}` - Get a specific order
- `POST /api/orders` - Create a new order
- `PUT /api/orders/{id}` - Update an order
- `DELETE /api/orders/{id}` - Delete an order
- `GET /api/orders/user/{userId}` - Get orders for a specific user

## Models

### Ticket

- `id` - Unique identifier
- `eventId` - Reference to the event
- `eventName` - Name of the event
- `venue` - Venue of the event
- `price` - Price of the ticket
- `available` - Availability status
- `userId` - Reference to the user who owns/bought this ticket

### Event

- `id` - Unique identifier
- `name` - Name of the event
- `description` - Description of the event
- `venue` - Venue of the event
- `eventDate` - Date and time of the event
- `ticketPrice` - Price of tickets for this event
- `totalTickets` - Total number of tickets available
- `availableTickets` - Number of tickets still available

### Order

- `id` - Unique identifier
- `userId` - Reference to the user who placed the order
- `orderItems` - List of items in the order
- `totalAmount` - Total amount of the order
- `status` - Status of the order
- `createdAt` - Creation timestamp
- `updatedAt` - Last update timestamp

### OrderItem

- `id` - Unique identifier
- `orderId` - Reference to the order
- `ticketId` - Reference to the ticket
- `quantity` - Number of tickets
- `price` - Price of the ticket at the time of purchase