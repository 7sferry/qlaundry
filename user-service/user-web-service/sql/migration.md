## tenants
- id
- name
- description

## staffs
- id
- username
- password
- full name
- description
- tenant_id

## staff_phones
- id
- phone_no
- staff_id

## staff_emails
- id
- email
- staff_id

## staff_addresses
- id
- address_line
- staff_id

## email_triggers
- id
- type (TENANT_REGISTRATION)
- recipient
- payload (json message published to the redis stream)
- status (CREATED | PUBLISHED)


