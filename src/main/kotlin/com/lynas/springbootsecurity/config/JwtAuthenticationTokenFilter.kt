package com.lynas.springbootsecurity.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


class JwtAuthenticationTokenFilter : OncePerRequestFilter() {

    @Value("\${jwt.header}")
    private val tokenHeader: String? = null

    @Autowired
    lateinit var jwtTokenUtil: JwtTokenUtil

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val authToken = request.getHeader(this.tokenHeader)
        val username = jwtTokenUtil.getUsernameFromToken(authToken)
        val rolesString = jwtTokenUtil.getUserRoleFromToken(authToken)
        val roles = AuthorityUtils.commaSeparatedStringToAuthorityList(rolesString)
        logger.info("checking authentication für user " + username!!)

        if (username != null && roles != null
                && SecurityContextHolder.getContext().authentication == null && jwtTokenUtil.validateToken(authToken)) {
            val jwtUser = JwtUser(null,username,null,null,null,"",roles,true)
            val authentication = UsernamePasswordAuthenticationToken(
                    jwtUser,
                    null,
                    roles)
            authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
            logger.info("authenticated user $username, setting security context")
            SecurityContextHolder.getContext().authentication = authentication
        }

        chain.doFilter(request, response)
    }

}