package in.co.helloworlds.security.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Set;

import static in.co.helloworlds.security.user.Permission.ADMIN_CREATE;
import static in.co.helloworlds.security.user.Permission.ADMIN_DELETE;
import static in.co.helloworlds.security.user.Permission.ADMIN_READ;
import static in.co.helloworlds.security.user.Permission.ADMIN_UPDATE;
import static in.co.helloworlds.security.user.Permission.MANAGER_CREATE;
import static in.co.helloworlds.security.user.Permission.MANAGER_DELETE;
import static in.co.helloworlds.security.user.Permission.MANAGER_READ;
import static in.co.helloworlds.security.user.Permission.MANAGER_UPDATE;

@RequiredArgsConstructor
public enum Role {

  USER(Collections.emptySet()),
  ADMIN(Set.of(ADMIN_READ, ADMIN_UPDATE, ADMIN_CREATE, ADMIN_DELETE)),
  MANAGER(Set.of(MANAGER_READ, MANAGER_UPDATE, MANAGER_CREATE, MANAGER_DELETE));

  @Getter
  private final Set<Permission> permissions;

  // public List<SimpleGrantedAuthority> getAuthorities() {
  // List<SimpleGrantedAuthority> authorities = getPermissions()
  // .stream()
  // .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
  // .collect(Collectors.toList());
  // authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
  // return authorities;
  // }
}