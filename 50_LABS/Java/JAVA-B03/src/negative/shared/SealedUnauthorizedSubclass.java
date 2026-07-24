sealed class SealedUnauthorizedSubclass permits Allowed {}
final class Allowed extends SealedUnauthorizedSubclass {}
final class Rejected extends SealedUnauthorizedSubclass {}
