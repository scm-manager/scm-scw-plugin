/*
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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

import javax.inject.Inject;
import java.util.Set;

@Extension
@EagerSingleton
public class PullRequestEventSubscriber {

  private final ScwResultService service;
  private final CommentService commentService;

  @Inject
  public PullRequestEventSubscriber(ScwResultService service, CommentService commentService) {
    this.service = service;
    this.commentService = commentService;
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
      for (ScwResult result : results) {
        if (shouldCreateReplyForResult(event, result)) {
          createReply(event, result);
        }
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

    // Add title
    text.append("**").append(result.getName()).append("**").append("\n\n");

    // Add description
    text.append(result.getDescription()).append("\n\n");

    // Add videos
    if (result.getVideos().size() > 0) {
      text.append("<iframe width=\"560\" height=\"315\"\n")
        .append("src=").append(result.getVideos().get(0).replace(" ", "+")).append(" \n")
        .append("frameborder=\"0\" \n")
        .append("allow=\"accelerometer; encrypted-media; gyroscope; picture-in-picture\" \n")
        .append("allowfullscreen=true></iframe> \n\n");
    }

    // Add training links
    text.append("[Secure Code Warrior Training](").append(result.getUrl()).append(")");

    return text.toString();
  }

}
