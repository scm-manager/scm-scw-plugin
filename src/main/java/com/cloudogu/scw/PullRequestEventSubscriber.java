/*
 * Copyright (c) 2020 - present Cloudogu GmbH
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 */

package com.cloudogu.scw;

import com.cloudogu.scm.review.comment.service.CommentEvent;
import com.cloudogu.scm.review.comment.service.CommentService;
import com.cloudogu.scm.review.comment.service.Reply;
import com.cloudogu.scm.review.pullrequest.service.PullRequestEvent;
import com.github.legman.Subscribe;
import sonia.scm.EagerSingleton;
import sonia.scm.HandlerEventType;
import sonia.scm.plugin.Extension;
import sonia.scm.web.security.AdministrationContext;

import jakarta.inject.Inject;
import java.util.Set;

@Extension
@EagerSingleton
public class PullRequestEventSubscriber {

  private final ScwResultService service;
  private final CommentService commentService;
  private final AdministrationContext administrationContext;

  @Inject
  public PullRequestEventSubscriber(ScwResultService service, CommentService commentService, AdministrationContext administrationContext) {
    this.service = service;
    this.commentService = commentService;
    this.administrationContext = administrationContext;
  }

  @Subscribe
  public void handleEvent(PullRequestEvent event) {
    if (shouldHandleEvent(event.getEventType())) {
      service.checkPullRequestForResults(event.getRepository(), event.getPullRequest());
    }
  }

  @Subscribe
  public void handleEvent(CommentEvent event) {
    if (shouldHandleEvent(event.getEventType())) {
      Set<ScwResult> results = service.checkTextForResults(event.getItem().getComment());
      if (!results.isEmpty()) {
        administrationContext.runAsAdmin(() -> {
          for (ScwResult result : results) {
            if (shouldCreateReplyForResult(event, result)) {
              createReply(event, result);
            }
          }
        });
      }
    }
  }

  private boolean shouldCreateReplyForResult(CommentEvent event, ScwResult result) {
    for (Reply reply : event.getItem().getReplies()) {
      if (reply.getComment().contains(result.getUrl())) {
        return false;
      }
    }
    return true;
  }

  private void createReply(CommentEvent event, ScwResult result) {
    Reply reply = new Reply();
    reply.setComment(createReplyText(result));
    reply.setSystemReply(true);
    commentService.reply(
      event.getRepository().getNamespace(),
      event.getRepository().getName(),
      event.getPullRequest().getId(),
      event.getItem().getId(),
      reply
    );
  }

  private boolean shouldHandleEvent(HandlerEventType eventType) {
    return eventType == HandlerEventType.CREATE || eventType == HandlerEventType.MODIFY;
  }

  private String createReplyText(ScwResult result) {

    StringBuilder text = new StringBuilder();

    // Add header
    text.append("*Information from Secure Code Warrior based on mentioned security issue:*\n\n");

    // Add title
    text.append("**").append(result.getName()).append("**").append("\n\n");

    // Add description
    text.append(result.getDescription()).append("\n\n");

    // Add videos
    if (!result.getVideos().isEmpty()) {
      // Add embedded video player
      text.append("[Video on ").append(result.getName()).append("](scw:").append(result.getVideos().get(0).replace(" ", "+")).append(") \n\n");
      // Add additional videos as links
      if (result.getVideos().size() > 1) {
        text.append("Check out this additional videos:\n\n");
        for (int i = 1; i < result.getVideos().size(); i++) {
          text.append("[Video").append(i).append("](").append(result.getVideos().get(i)).append(")");
        }
      }
    }

    // Add training links
    text.append("[Try this challenge on Secure Code Warrior](").append(result.getUrl()).append(")");

    return text.toString();
  }

}
