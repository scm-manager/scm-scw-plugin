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

import com.cloudogu.scm.review.comment.service.Comment;
import com.cloudogu.scm.review.comment.service.CommentEvent;
import com.cloudogu.scm.review.comment.service.CommentService;
import com.cloudogu.scm.review.comment.service.Location;
import com.cloudogu.scm.review.comment.service.Reply;
import com.cloudogu.scm.review.pullrequest.service.PullRequest;
import com.cloudogu.scm.review.pullrequest.service.PullRequestEvent;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sonia.scm.HandlerEventType;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryTestData;
import sonia.scm.web.security.AdministrationContext;
import sonia.scm.web.security.PrivilegedAction;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PullRequestEventSubscriberTest {

  private static final Repository REPOSITORY = RepositoryTestData.create42Puzzle();

  @Mock
  private CommentService commentService;

  @Mock
  private ScwResultService service;

  private PullRequestEventSubscriber eventSubscriber;

  @BeforeEach
  void initSubscriber() {
    AdministrationContext administrationContext = new AdministrationContext() {
      @Override
      public void runAsAdmin(PrivilegedAction action) {
        action.run();
      }

      @Override
      public void runAsAdmin(Class<? extends PrivilegedAction> actionClass) {
        // Nothing
      }
    };
    eventSubscriber = new PullRequestEventSubscriber(service, commentService, administrationContext);
  }

  @Test
  void shouldHandlePullRequestUpdates() {
    PullRequest pr = new PullRequest();
    eventSubscriber.handleEvent(new PullRequestEvent(REPOSITORY, pr, pr, HandlerEventType.CREATE));

    verify(service).checkPullRequestForResults(REPOSITORY, pr);
  }

  @Test
  void shouldNotHandleCommentEvent() {
    PullRequest pr = new PullRequest();
    Comment comment = new Comment();
    eventSubscriber.handleEvent(new CommentEvent(REPOSITORY, pr, comment, comment, HandlerEventType.DELETE));

    verify(commentService, never()).reply(any(), any(), any(), any(), any());
  }

  @Test
  void shouldHandleCommentEvent() {
    String text = "Beware sqli!";
    when(service.checkTextForResults(text))
      .thenReturn(ImmutableSet.of(new ScwResult("url", "name", "desc", ImmutableList.of("some video"))));

    PullRequest pr = new PullRequest("1", "source", "target");
    Comment comment = Comment.createComment("a1", text, "trillian", new Location("readme.md"));
    eventSubscriber.handleEvent(new CommentEvent(REPOSITORY, pr, comment, comment, HandlerEventType.CREATE));

    verify(commentService).reply(eq(REPOSITORY.getNamespace()), eq(REPOSITORY.getName()), eq(pr.getId()), eq(comment.getId()), any(Reply.class));
  }

  @Test
  void shouldNotAddReplyToCommentIfSimilarReplyAlreadyExists() {
    String text = "Beware sqli!";
    when(service.checkTextForResults(text))
      .thenReturn(ImmutableSet.of(new ScwResult("url", "name", "desc", ImmutableList.of("some video"))));

    PullRequest pr = new PullRequest("1", "source", "target");
    Comment comment = Comment.createComment("a1", text, "trillian", new Location("readme.md"));
    Reply reply = new Reply();
    reply.setComment("url");
    comment.addReply(reply);

    // Should not add reply if reply with url already exists
    eventSubscriber.handleEvent(new CommentEvent(REPOSITORY, pr, comment, comment, HandlerEventType.CREATE));

    verify(commentService, never()).reply(eq(REPOSITORY.getNamespace()), eq(REPOSITORY.getName()), eq(pr.getId()), eq(comment.getId()), any(Reply.class));
  }
}
