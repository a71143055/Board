package com.example.board.service;

import com.example.board.domain.Member;
import com.example.board.domain.Role;
import com.example.board.repository.MemberRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(String username, String rawPassword, String email) {
        Member m = new Member(username, passwordEncoder.encode(rawPassword), email, Role.ROLE_USER);
        memberRepository.save(m);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        return new User(
                member.getUsername(),
                member.getPassword(),
                List.of(new SimpleGrantedAuthority(member.getRole().name()))
        );
    }
}
