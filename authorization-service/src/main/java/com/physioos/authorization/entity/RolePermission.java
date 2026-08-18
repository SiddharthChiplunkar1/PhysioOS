package com.physioos.authorization.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="role_permissions",uniqueConstraints= {@UniqueConstraint(columnNames= {"role_id","permission_id"})})
@Getter
@Setter
@NoArgsConstructor
public class RolePermission {
	@Id
	@GeneratedValue(strategy=GenerationType.UUID)
	private java.util.UUID id;
	@ManyToOne(fetch=FetchType.LAZY,optional=false)
	@JoinColumn(name="role_id",nullable=false)
	private Role role;
	@ManyToOne(fetch=FetchType.LAZY,optional=false)
	@JoinColumn(name="permission_id",nullable=false)
	private Permission permission;
	private String condition;
	public RolePermission(Role role,Permission permission) {
		this.role=role;
		this.permission=permission;
	}
	
	
}
