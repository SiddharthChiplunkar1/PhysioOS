package com.physioos.authorization.entity;
import com.physioos.authorization.enums.PermissionName;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="permission")
public class Permission {
	@Id
	@GeneratedValue(strategy=GenerationType.UUID)
	private java.util.UUID permissionId;
	@Enumerated(EnumType.STRING)
	@Column(name="permission_name",nullable=false)
	private PermissionName permissionName;
	
}
