package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.CommunityGroup
import com.example.data.model.CommunityPost
import com.example.data.model.SupportTicket
import com.example.ui.KoreLifeViewModel
import com.example.ui.theme.*

@Composable
fun SocialScreen(
    viewModel: KoreLifeViewModel,
    modifier: Modifier = Modifier
) {
    val posts by viewModel.posts.collectAsState()
    val groups by viewModel.groups.collectAsState()
    val tickets by viewModel.tickets.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("Comunidad", "Grupos", "Soporte")

    var showCreatePostDialog by remember { mutableStateOf(false) }
    var showCreateTicketDialog by remember { mutableStateOf(false) }

    if (showCreatePostDialog) {
        CreatePostDialog(
            onDismiss = { showCreatePostDialog = false },
            onSave = { content, cat ->
                viewModel.addPost(content, cat)
            }
        )
    }

    if (showCreateTicketDialog) {
        CreateTicketDialog(
            onDismiss = { showCreateTicketDialog = false },
            onSave = { subj, cat ->
                viewModel.createSupportTicket(subj, cat)
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selectedTab == 0) showCreatePostDialog = true
                    else if (selectedTab == 2) showCreateTicketDialog = true
                },
                containerColor = EmeraldPrimary,
                contentColor = EmeraldOnPrimary,
                modifier = Modifier
                    .padding(bottom = 70.dp)
                    .testTag("social_fab_action")
            ) {
                Icon(if (selectedTab == 2) Icons.Default.HelpOutline else Icons.Default.Edit, contentDescription = "Acción")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Comunidad & Conexión", color = TextWhite, fontSize = 24.sp, fontWeight = FontWeight.Black)
            }

            // Tabs
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tabTitles.indices.toList()) { index ->
                    val isSelected = selectedTab == index
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) EmeraldPrimary else DarkSurfaceVariant)
                            .border(1.dp, if (isSelected) EmeraldPrimary else DarkBorder, RoundedCornerShape(12.dp))
                            .clickable { selectedTab = index }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = tabTitles[index],
                            color = if (isSelected) EmeraldOnPrimary else TextWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    0 -> CommunityFeedTab(posts = posts, viewModel = viewModel, onNewPost = { showCreatePostDialog = true })
                    1 -> GroupsTab(groups = groups, viewModel = viewModel)
                    2 -> SupportTab(tickets = tickets, onNewTicket = { showCreateTicketDialog = true })
                }
            }
        }
    }
}

@Composable
private fun CommunityFeedTab(
    posts: List<CommunityPost>,
    viewModel: KoreLifeViewModel,
    onNewPost: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        items(posts) { post ->
            var showCommentInput by remember { mutableStateOf(false) }
            var commentText by remember { mutableStateOf("") }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Post Author Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF1E1B4B))
                                    .border(1.dp, IndigoSecondary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = post.initials, color = Color(0xFFA5B4FC), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = post.author, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text(text = "${post.category} • ${post.timeAgo}", color = TextMuted, fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(text = post.content, color = TextWhite, fontSize = 14.sp, lineHeight = 20.sp)

                    Spacer(modifier = Modifier.height(14.dp))

                    // Post Actions (Like, Comment)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { viewModel.toggleLikePost(post.id) }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = if (post.likedByMe) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Me gusta",
                                tint = if (post.likedByMe) RedError else TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "${post.likes}", color = if (post.likedByMe) RedError else TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { showCommentInput = !showCommentInput }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ChatBubbleOutline,
                                contentDescription = "Comentar",
                                tint = TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "${post.comments.size}", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Comments section
                    if (post.comments.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkSurfaceVariant)
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            post.comments.forEach { comment ->
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = comment.author, color = EmeraldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text(text = comment.timeAgo, color = TextSubtle, fontSize = 9.sp)
                                    }
                                    Text(text = comment.text, color = TextWhite, fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    // Comment Input
                    if (showCommentInput) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = commentText,
                                onValueChange = { commentText = it },
                                placeholder = { Text("Escribe una respuesta...", color = TextSubtle, fontSize = 12.sp) },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite,
                                    focusedBorderColor = EmeraldPrimary,
                                    unfocusedBorderColor = DarkBorder
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = {
                                    if (commentText.isNotBlank()) {
                                        viewModel.addCommentToPost(post.id, commentText)
                                        commentText = ""
                                        showCommentInput = false
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Send, contentDescription = "Enviar", tint = EmeraldPrimary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GroupsTab(
    groups: List<CommunityGroup>,
    viewModel: KoreLifeViewModel
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        items(groups) { group ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = group.name, color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text(text = "${group.membersCount} miembros • ${group.category}", color = TextMuted, fontSize = 11.sp)
                        }

                        Button(
                            onClick = { viewModel.toggleJoinGroup(group.id) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (group.isJoined) DarkSurfaceVariant else EmeraldPrimary,
                                contentColor = if (group.isJoined) TextWhite else EmeraldOnPrimary
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(if (group.isJoined) "Unido ✓" else "Unirse", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = group.description, color = TextMuted, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun SupportTab(
    tickets: List<SupportTicket>,
    onNewTicket: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item {
            Button(
                onClick = onNewTicket,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant, contentColor = EmeraldPrimary)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Crear Nuevo Ticket de Ayuda", fontWeight = FontWeight.Bold)
            }
        }

        items(tickets) { ticket ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = ticket.subject, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(text = "${ticket.category} • ${ticket.date}", color = TextMuted, fontSize = 11.sp)
                    }

                    val isResolved = ticket.status == "Resuelto"
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isResolved) Color(0xFF064E3B) else Color(0xFF78350F))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = ticket.status,
                            color = if (isResolved) EmeraldPrimary else AmberTertiary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CreatePostDialog(
    onDismiss: () -> Unit,
    onSave: (content: String, category: String) -> Unit
) {
    var content by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Productividad") }
    val categories = listOf("Productividad", "Ahorro", "Inversiones", "Metas")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Compartir con la Comunidad", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    placeholder = { Text("¿Qué logro o hábito quieres compartir hoy?", color = TextMuted) },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    categories.forEach { cat ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selectedCategory == cat) EmeraldPrimary else DarkSurfaceVariant)
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(text = cat, color = if (selectedCategory == cat) EmeraldOnPrimary else TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancelar") }
                    Button(
                        onClick = {
                            if (content.isNotBlank()) {
                                onSave(content, selectedCategory)
                                onDismiss()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = EmeraldOnPrimary)
                    ) {
                        Text("Publicar", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun CreateTicketDialog(
    onDismiss: () -> Unit,
    onSave: (subject: String, category: String) -> Unit
) {
    var subject by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Finanzas") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Crear Ticket de Soporte", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Asunto o problema", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancelar") }
                    Button(
                        onClick = {
                            if (subject.isNotBlank()) {
                                onSave(subject, category)
                                onDismiss()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = EmeraldOnPrimary)
                    ) {
                        Text("Enviar Ticket", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
