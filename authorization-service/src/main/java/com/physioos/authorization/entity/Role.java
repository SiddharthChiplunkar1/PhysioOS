package com.physioos.authorization.entity;
import com.physioos.authorization.enums.RoleName;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
@Getter
@Setter
@NoArgsConstructor

@Entity
@Table(name="role")
public class Role {
	@Id
	@GeneratedValue(strategy=GenerationType.UUID)
	private java.util.UUID roleId;//i dont know
	
	@Column(name="organisation_id",nullable=false)
	private java.util.UUID organisationId;
	@Column(name="role_name")
	@Enumerated(EnumType.STRING)
	private RoleName roleName;
}
