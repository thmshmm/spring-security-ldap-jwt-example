# Example Spring WebFlux Application with LDAP+JWT Security

Demo using an embedded LDAP server to test multiple authentication managers.

User / Password (groups):
* user1 / user1 (users)
* admin / admin (admins, users)

LDAP groups are mapped into the JWT token as roles which are used to authorize the endpoints.

API:
* /auth/login (issues JWT tokens)
* /api/hello (accessible as admin and user1)
* /api/hello-admin (accessible only as admin)

TODO:
* JWT token renewal